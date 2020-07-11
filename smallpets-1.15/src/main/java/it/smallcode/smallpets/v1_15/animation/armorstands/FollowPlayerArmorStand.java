package it.smallcode.smallpets.v1_15.animation.armorstands;
/*

Class created by SmallCode
02.07.2020 17:03

*/
import it.smallcode.smallpets.animations.FollowPlayerAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class FollowPlayerArmorStand extends FollowPlayerAnimation {

    private ArmorStand armorStand;

    public FollowPlayerArmorStand(ArmorStand armorStand, double speed){

        super(speed);

        this.armorStand = armorStand;

    }

    @Override
    public Location move(Player player, Location loc) {

        Vector goal = vectorFromLocation(player.getLocation());

        goal.setY(goal.getY() + 0.75);

        Vector start = vectorFromLocation(loc);

        Vector direction = normalize(goal.subtract(start));

        Location newLoc = loc.clone();

        newLoc.add(direction.multiply(speed));

        try {

            if(!newLoc.getChunk().isLoaded())
                newLoc.getChunk().load();

            if(!loc.getChunk().isLoaded())
                loc.getChunk().load();

            armorStand.teleport(newLoc);

            return newLoc;

        }catch (Exception ex){

            ex.printStackTrace();

            return loc;

        }

    }
}
