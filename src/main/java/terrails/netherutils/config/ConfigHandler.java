package terrails.netherutils.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static final String FIRST_SPAWN = "First Spawn";
    public static final String FEATURES = "Features";
    public static final String FEATURES_NETHER = FEATURES + "." + "Nether";
    public static final String FEATURES_TANK = FEATURES + "." + "Tank";
    public static final String FEATURES_PORTAL = FEATURES + "." + "Portal";
    public static final String GENERATION = "Generation";

    // Boolean
    public static boolean pointRespawn;
    public static boolean generateHellWood;
    public static boolean generateAshWood;
    public static boolean generateSoulWood;
    public static boolean tankKeepContent;
    public static boolean portalKeepFluid;
    public static boolean portalKeepInventory;
    public static boolean vanillaPortal;
    public static boolean portalDebugTool;

    // Integer
    public static int minTankWater;
    public static int portalCapacity;
    public static int portalFuelUsage;
    public static int portalActivationFuelUsage;

    // String
    public static String portalFuel;
    public static String portalPedestalItem;
    public static String itemToLeave;

    // Other
    public static String[] startingItems;
    public static String[] startingEffects;
    public static String[] portalItems;

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
        pointRespawn = config.get(FEATURES_NETHER, "Nether Spawn", true, "Disable if you don't want to spawn in the nether").getBoolean();
        vanillaPortal = config.get(FEATURES_NETHER, "Use Vanilla Portal", false, "Leave the vanilla portal as it is and don't disable it, Nether Spawn needs to be true for this to work").getBoolean();
        itemToLeave = config.get(FEATURES_NETHER, "Item To Leave", "minecraft:beacon", "The item which needs to be made before being able to leave the nether (empty if nothing), Nether Spawn needs to be true for this to work").getString();

        startingItems = config.getStringList("Starting Items", FIRST_SPAWN, DEFAULT_ITEMS, "The List of Starting Items");
        startingEffects = config.getStringList("Starting Effects", FIRST_SPAWN, DEFAULT_EFFECTS, "The List of Starting Effects");

        generateHellWood = config.get(GENERATION, "Generate Hellwood", true, "Enable the generation of hellwood tree").getBoolean();
        generateAshWood = config.get(GENERATION, "Generate Ashwood", true, "Enable the generation of ashwood tree").getBoolean();
        generateSoulWood = config.get(GENERATION, "Generate Soulwood", true, "Enable the generation of soulwood tree").getBoolean();

        minTankWater = config.get(FEATURES_TANK, "Min Tank Water", 3000, "The minimal amount of water in mB required for the fluid tank to turn into a block that hydrates land (0 to disable)", 0, 4000).getInt();
        tankKeepContent = config.get(FEATURES_TANK, "Keep Fluid", true, "Keep the fluid when tank is broken and put it in the block when placed again").getBoolean();

        portalItems = config.getStringList("Items", FEATURES_PORTAL, DEFAULT_PORTAL_ITEMS,"The items required to turn on the portal");
        portalFuel = config.get(FEATURES_PORTAL, "Fuel","water", "Liquid which is required for the portal to run").getString();
        portalCapacity = config.get(FEATURES_PORTAL, "Capacity", 5000, "Capacity of the portal in mB").getInt();
        portalFuelUsage = config.get(FEATURES_PORTAL, "Usage", 10, "The time after which the portal will use 1 mB of fuel (seconds)").getInt();
        portalActivationFuelUsage = config.get(FEATURES_PORTAL, "Activation Usage", 15, "The amount of fuel the portal will use each tick when activating (1 second = 20 ticks)").getInt();
        portalKeepFluid = config.get(FEATURES_PORTAL, "Keep Fluid", true, "Keep the fluid when portal is broken and put it in the block when placed again").getBoolean();
        portalKeepInventory = config.get(FEATURES_PORTAL, "Keep Inventory", true, "Keep the inventory when portal is broken and put it in the block when placed again").getBoolean();
        portalPedestalItem = config.get(FEATURES_PORTAL, "Pedestal Item", "minecraft:water_bucket", "The item which needs to be on four pedestals around the portal").getString();
        portalDebugTool = config.get(FEATURES_PORTAL, "Debug Tool", false, "Enable the debug tool to see additional portal data and amount of master portals in the world (Prints a message in chat when right clicking a portal)").getBoolean();

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
