package it.smallcode.smallpets.v1_15;
/*

Class created by SmallCode
03.07.2020 16:21

*/

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.netty.buffer.ByteBuf;
import it.smallcode.smallpets.events.PetLevelUpEvent;
import it.smallcode.smallpets.pets.Pet;
import it.smallcode.smallpets.v1_15.animation.FollowPlayerArmorStand;
import it.smallcode.smallpets.v1_15.animation.HoverArmorStand;
import it.smallcode.smallpets.v1_15.animation.LevelOnehundretAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.Wrapper;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * The foundation of all pets in the 1.15 version
 *
 */
public class SamplePet extends Pet {

    private static List<Integer> entityIDs = new LinkedList<>();

    /**
     *
     * Creates a pet
     *
     * @param owner - the pet owner
     * @param xp - the xp
     */
    public SamplePet(Player owner, Long xp) {
        super(owner, xp);
    }

    private FollowPlayerArmorStand followPlayerArmorStand;
    private HoverArmorStand hoverArmorStand;
    private LevelOnehundretAnimation levelOnehundretAnimation;

    private int rotateID;

    private int entityID = -1;

    public void spawn(JavaPlugin plugin) {

        Location loc = owner.getLocation().clone();

        loc.setX(loc.getX() - 1);
        loc.setY(loc.getY() + 0.75);

        spawnPackets(loc);

    }

    private void spawnPackets(Location loc){

        do{

            entityID = (int) (Math.random() * 10000);

        }while(entityIDs.contains(entityID) && entityID >= 0);

        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity();

        spawnEntityPacket.setEntityID(entityID);
        spawnEntityPacket.setType(EntityType.ARMOR_STAND);

        spawnEntityPacket.setX(loc.getX());
        spawnEntityPacket.setY(loc.getY());
        spawnEntityPacket.setZ(loc.getZ());

        sendPacket(spawnEntityPacket.getHandle());

        WrapperPlayServerEntityEquipment entityEquipmentPacket = new WrapperPlayServerEntityEquipment();

        entityEquipmentPacket.setEntityID(entityID);
        entityEquipmentPacket.setSlot(EnumWrappers.ItemSlot.HEAD);
        entityEquipmentPacket.setItem(getItem());

        sendPacket(entityEquipmentPacket.getHandle());

        WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata();

        entityMetadataPacket.setEntityID(entityID);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMetadataPacket.getMetadata());

        dataWatcher.setObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)),
                (byte) 0x01);

        dataWatcher.setObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)),
                (byte) 0x20);

        dataWatcher.setObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)),
                "§8[" + getLevelColor() + getLevel() + "§8] §7" + owner.getName() + "s " + getName());

        dataWatcher.setObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),
                true);

        dataWatcher.setObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)),
                true);

        entityMetadataPacket.setMetadata(dataWatcher.getWatchableObjects());

        sendPacket(entityMetadataPacket.getHandle());

    }

    private void sendPacket(PacketContainer packet){

        for(Player all : Bukkit.getOnlinePlayers()){

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(all, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    private void spawnArmorStand(JavaPlugin plugin, Location loc){

        armorStand = createArmorStand(loc);

        //Please don't ask why

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                armorStand.setCustomNameVisible(true);
            }
        }, 1);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                armorStand.setCustomName("§8[" + getLevelColor() + getLevel() + "§8] §7" + owner.getName() + "s " + getName());
            }
        }, 2);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                armorStand.setGravity(false);
            }
        }, 3);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                armorStand.setSmall(true);
            }
        }, 4);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                armorStand.setInvulnerable(true);
            }
        }, 5);

        armorStand.getEquipment().setHelmet(getItem());

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                armorStand.setVisible(false);

            }
        }, 6);

        initAnimations(plugin);

    }

    protected void initAnimations(JavaPlugin plugin){

        followPlayerArmorStand = new FollowPlayerArmorStand(armorStand, 0.5, owner, plugin);
        followPlayerArmorStand.setActive(false);

        hoverArmorStand = new HoverArmorStand(armorStand, 0.025, 0.2, -0.5, plugin);
        hoverArmorStand.setActive(false);

        if(getLevel() == 100)
            levelOnehundretAnimation = new LevelOnehundretAnimation(this, armorStand, plugin);

        rotateID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                Location loc = armorStand.getLocation().clone();

                double a = owner.getLocation().getX() - armorStand.getLocation().getX();
                double b = owner.getLocation().getZ() - armorStand.getLocation().getZ();

                double angle = Math.atan(b / a);

                angle = angle * (180 / Math.PI);

                if(owner.getLocation().getX() - armorStand.getLocation().getX() >= 0){

                    angle += 180;

                }

                angle += 90;

                loc.setYaw((float) angle);

                try {

                    if(!loc.getChunk().isLoaded())
                        loc.getChunk().load();

                    if(!armorStand.getLocation().getChunk().isLoaded())
                        armorStand.getLocation().getChunk().load();

                    armorStand.teleport(loc);

                }catch (Exception ex){ ex.printStackTrace(); }

                Location particleLoc = loc.clone();

                particleLoc.setY(particleLoc.getY() + 0.7);

                if(!particleLoc.getChunk().isLoaded())
                    particleLoc.getChunk().load();

                particleLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLoc, 1);

                if(armorStand.getLocation().distance(owner.getLocation()) >= 2.5D)
                    move();
                else
                    idle();

            }
        }, 0, 0);

    }

    public void move() {

        followPlayerArmorStand.setActive(true);

        hoverArmorStand.setActive(false);

    }

    public void idle() {

        followPlayerArmorStand.setActive(false);

        hoverArmorStand.setActive(true);

    }

    public void giveExp(int exp, JavaPlugin plugin){

        int level = getLevel();

        if(level != 100)
            this.xp += exp;

        if(level < getLevel()){

            //LEVEL UP

            Bukkit.getPluginManager().callEvent(new PetLevelUpEvent(this));

            armorStand.setCustomName("§8[" + getLevelColor() + getLevel() + "§8] §7" + owner.getName() + "s " + getName());

            if(getLevel() == 100)
                levelOnehundretAnimation = new LevelOnehundretAnimation(this, armorStand, plugin);

        }

    }

    public void destroy() {

        Bukkit.getScheduler().cancelTask(rotateID);

        followPlayerArmorStand.cancel();
        hoverArmorStand.cancel();

        if(levelOnehundretAnimation != null)
            levelOnehundretAnimation.cancel();

        if(armorStand != null)
            armorStand.remove();

    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void registerRecipe(Plugin plugin) {

    }

    @Override
    public ItemStack getUnlockItem(Plugin plugin) {
        return null;
    }

    @Override
    public String getAbility() {
        return "";
    }

    protected ArmorStand createArmorStand(Location loc){

        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        return armorStand;

    }

    public String getName() {
        return null;
    }

}