package terrails.netherutils.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static final String GENERAL_SETTINGS = "General Settings";
    public static final String FIRST_SPAWN = GENERAL_SETTINGS + ".First Spawn";
    public static final String FEATURES = GENERAL_SETTINGS + ".Features";
    public static final String FEATURES_PORTAL = FEATURES + ".Portal";
    public static final String GENERATION = GENERAL_SETTINGS + ".Generation";

    // Boolean
    public static boolean pointRespawn;
    public static boolean generateHellWood;
    public static boolean generateAshWood;
    public static boolean generateSoulWood;

    // Integer
    public static int minTankWater;


    // Other
    public static String[] startingItems;
    public static String[] startingEffects;

    public static String[] portalItems;
    public static String portalFuel;
    public static int portalCapacity;
    public static int portalFuelUsage;


    public static void init(File dir) {
        config = new Configuration(new File(dir, Constants.MOD_ID + ".cfg"));
        syncConfig();
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Constants.MOD_ID)) {
            syncConfig();
        }
    }

    public static void syncConfig(){
        pointRespawn = config.get(GENERAL_SETTINGS, "Nether Spawn", true, "Disable if you don't want to spawn in the nether").getBoolean();

        minTankWater = config.get(FEATURES, "Min Tank Water", 3000, "The minimal amount of water in mB required for the fluid tank to turn into a block that hydrates land (0 to disable)", 0, 4000).getInt();

        startingItems = config.getStringList("Starting Items", FIRST_SPAWN, DEFAULT_ITEMS, "The List of Starting Items");
        startingEffects = config.getStringList("Starting Effects", FIRST_SPAWN, DEFAULT_EFFECTS, "The List of Starting Effects");

        generateHellWood = config.get(GENERATION, "Generate Hellwood", true, "Enable the generation of hellwood tree").getBoolean();
        generateAshWood = config.get(GENERATION, "Generate Ashwood", true, "Enable the generation of ashwood tree").getBoolean();
        generateSoulWood = config.get(GENERATION, "Generate Soulwood", true, "Enable the generation of soulwood tree").getBoolean();

        portalItems = config.getStringList("Items", FEATURES_PORTAL, DEFAULT_PORTAL_ITEMS,"The items required to turn on the portal");
        portalFuel = config.get(FEATURES_PORTAL, "Fuel","water", "Liquid which is required for the portal to run").getString();
        portalCapacity = config.get(FEATURES_PORTAL, "Capacity", 5000, "Capacity of the portal in mB").getInt();
        portalFuelUsage = config.get(FEATURES_PORTAL, "Usage", 10, "The time after which the portal will use 1 mB of fuel (seconds)").getInt();

        if (config.hasChanged()) {
            config.save();
        }
    }

    private final static String[] DEFAULT_ITEMS = {
            "minecraft:stone_pickaxe -enchantment:unbreaking:2 -name:'Start Pick'"
    };
    private final static String[] DEFAULT_EFFECTS = {
            "minecraft:fire_resistance -time:150"
    };
    private final static String[] DEFAULT_PORTAL_ITEMS = {
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl",
            "minecraft:blaze_rod, minecraft:enchanted_book, minecraft:blaze_rod",
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl"
    };
}
