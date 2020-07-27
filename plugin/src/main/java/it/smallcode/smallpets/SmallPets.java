package it.smallcode.smallpets;
/*

Class created by SmallCode
02.07.2020 13:34

*/

import it.smallcode.smallpets.cmds.SmallPetsCMD;
import it.smallcode.smallpets.listener.JoinListener;
import it.smallcode.smallpets.listener.QuitListener;
import it.smallcode.smallpets.listener.WorldSaveListener;
import it.smallcode.smallpets.manager.*;
import it.smallcode.smallpets.metrics.Metrics;
import it.smallcode.smallpets.v1_12.InventoryManager1_12;
import it.smallcode.smallpets.v1_12.ListenerManager1_12;
import it.smallcode.smallpets.v1_12.PetMapManager1_12;
import it.smallcode.smallpets.v1_13.InventoryManager1_13;
import it.smallcode.smallpets.v1_13.ListenerManager1_13;
import it.smallcode.smallpets.v1_13.PetMapManager1_13;
import it.smallcode.smallpets.v1_15.InventoryManager1_15;
import it.smallcode.smallpets.v1_15.ListenerManager1_15;
import it.smallcode.smallpets.v1_15.PetMapManager1_15;
import it.smallcode.smallpets.v1_16.InventoryManager1_16;
import it.smallcode.smallpets.v1_16.ListenerManager1_16;
import it.smallcode.smallpets.v1_16.PetMapManager1_16;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SmallPets extends JavaPlugin {

    private static SmallPets instance;

    private PetMapManager petMapManager;

    private UserManager userManager;

    private InventoryManager inventoryManager;
    private InventoryCache inventoryCache;

    private ListenerManager listenerManager;

    public final String PREFIX = "§b§lEagle§7§lCraft ";

    private double xpMultiplier;

    public static boolean useProtocolLib = false;

    @Override
    public void onEnable() {

        instance = this;

        this.loadConfig();

        inventoryCache = new InventoryCache();

        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().getPlugin("ProtocolLib").isEnabled()){

            String version = Bukkit.getServer().getClass().getPackage().getName();

            version = version.substring(version.lastIndexOf('.'));

            version = version.replace(".v", "");

            if(!version.startsWith("1_16")) {

                useProtocolLib = true;

                Bukkit.getConsoleSender().sendMessage(PREFIX + "Znaleziono Protocollib. Używam go.");

            }

        }

        if(!selectRightVersion())
            return;

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Rejestracja zwierzaki...");

        petMapManager.registerPets();

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Zarejestrowano zwierzaki");

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Rejestracja craftingu...");

        petMapManager.registerCraftingRecipe(this);

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Zarejestrowano crafting!");

        //Registering all listeners

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Rejestracja listenerów...");

        listenerManager.registerListener();

        Bukkit.getPluginManager().registerEvents(new JoinListener(userManager, petMapManager), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(userManager, inventoryCache), this);
        Bukkit.getPluginManager().registerEvents(new WorldSaveListener(), this);

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Zarejetrowano listenery!");

        //Registering all commands

        Bukkit.getPluginCommand("smallpets").setExecutor(new SmallPetsCMD());

        //Registering bStats

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Włączam metryczkę...");

        Metrics metrics = new Metrics(this, 8071);

        metrics.addCustomChart(new Metrics.DrilldownPie("protocollib", () ->{

            Map<String, Map<String, Integer>> map = new HashMap<>();

            String version = Bukkit.getVersion();

            Map<String, Integer> entry = new HashMap<>();

            entry.put(version, 1);

            if(useProtocolLib){

                map.put("Uses protocollib", entry);

            }else{

                map.put("Doesn't use protocollib", entry);

            }

            return map;

        }));

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Metryczka włączona!");

        //Loading the users which are online

        for(Player all : Bukkit.getOnlinePlayers()){

            userManager.loadUser(all.getUniqueId().toString(), petMapManager);

        }

        Bukkit.getPluginManager().registerEvents(new WorldSaveListener(), this);

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Plugin włączony");

    }

    @Override
    public void onDisable() {

        userManager.despawnPets();
        userManager.saveUsers();

        inventoryCache.removeAll();

    }

    public void loadConfig(){

        FileConfiguration cfg = this.getConfig();

        cfg.addDefault("xpMultiplier", 1D);

        getConfig().options().copyDefaults(true);

        saveConfig();
        reloadConfig();

        this.xpMultiplier = cfg.getDouble("xpMultiplier");

    }

    /**
     *
     * Selects the right version for the server
     *
     * @return a boolean if the right version was found
     */
    private boolean selectRightVersion(){

        String version = Bukkit.getServer().getClass().getPackage().getName();

        version = version.substring(version.lastIndexOf('.'));

        version = version.replace(".v", "");

        if(version.startsWith("1_12")) {

            petMapManager = new PetMapManager1_12();
            inventoryManager = new InventoryManager1_12(inventoryCache);
            userManager = new UserManager(this, petMapManager, useProtocolLib);
            listenerManager = new ListenerManager1_12(this, getUserManager(), getPetMapManager(), getInventoryCache(), PREFIX, xpMultiplier, useProtocolLib);

        }else if(version.startsWith("1_13")){

            petMapManager = new PetMapManager1_13();
            inventoryManager = new InventoryManager1_13(inventoryCache);
            userManager = new UserManager(this, petMapManager, useProtocolLib);
            listenerManager = new ListenerManager1_13(this, getUserManager(), getPetMapManager(), getInventoryCache(), PREFIX, xpMultiplier, useProtocolLib);

        }else if(version.startsWith("1_15") || version.startsWith("1_14")){

            petMapManager = new PetMapManager1_15();
            inventoryManager = new InventoryManager1_15(inventoryCache);
            userManager = new UserManager(this, petMapManager, useProtocolLib);
            listenerManager = new ListenerManager1_15(this, getUserManager(), getPetMapManager(), getInventoryCache(), PREFIX, xpMultiplier, useProtocolLib);

        }else if(version.startsWith("1_16")){

            petMapManager = new PetMapManager1_16();
            inventoryManager = new InventoryManager1_16(inventoryCache);
            userManager = new UserManager(this, petMapManager, useProtocolLib);
            listenerManager = new ListenerManager1_16(this, getUserManager(), getPetMapManager(), getInventoryCache(), PREFIX, xpMultiplier, useProtocolLib);

        }else{

            Bukkit.getConsoleSender().sendMessage(PREFIX + "Not supported version");

            Bukkit.getPluginManager().disablePlugin(this);

            return false;

        }

        Bukkit.getConsoleSender().sendMessage(PREFIX + "Loaded version " + version + "!");

        return true;

    }

    public static SmallPets getInstance() {
        return instance;
    }

    public PetMapManager getPetMapManager() {
        return petMapManager;
    }

    public InventoryCache getInventoryCache() {
        return inventoryCache;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
