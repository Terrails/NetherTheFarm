package terrails.netherutils.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static final String FIRST_SPAWN = "First Spawn";
    public static final String FEATURES = "Features";
    public static final String FEATURES_NETHER = FEATURES + "." + "Nether";
    public static final String FEATURES_END = FEATURES + "." + "End";
    public static final String FEATURES_TANK = FEATURES + "." + "Tank";
    public static final String FEATURES_PORTAL = FEATURES + "." + "Portal";
    public static final String FEATURES_NETHER_PORTAL = FEATURES_PORTAL + "." + "Nether";
    public static final String FEATURES_END_PORTAL = FEATURES_PORTAL + "." + "End";
    public static final String GENERATION = "Generation";

    /* == Nether Portal == */
    public static String netherPortalFuel;
    public static String netherPortalPedestalItem;
    public static boolean vanillaPortal;
    public static boolean netherPortalKeepFluid;
    public static boolean netherPortalKeepInventory;
    public static int netherPortalCapacity;
    public static int netherPortalFuelUsage;
    public static int netherPortalActivationFuelUsage;
    public static String[] netherPortalItems;
    
    /* == End Portal == */
    public static String endPortalFuel;
    public static String endPortalPedestalItem;
    public static boolean useVanillaEndPortal;
    public static boolean endPortalKeepFluid;
    public static boolean endPortalKeepInventory;
    public static int endPortalCapacity;
    public static int endPortalFuelUsage;
    public static int endPortalActivationFuelUsage;
    public static String[] endPortalItems;

    // Boolean
    public static boolean pointRespawn;
    public static boolean generateHellWood;
    public static boolean generateAshWood;
    public static boolean generateSoulWood;
    public static boolean tankKeepContent;
    public static boolean portalDebugTool;

    // Integer
    public static int minTankWater;
    public static int maxYNether;
    public static int deathZoneTimer;

    // String
    public static String itemToLeaveNether;
    public static String showWarningOnSec;

    // Other
    public static String[] startingItems;
    public static String[] startingEffects;

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
        itemToLeaveNether = config.get(FEATURES_NETHER, "Item To Leave", "minecraft:beacon", "The item which needs to be made before being able to leave the nether (empty if nothing), Nether Spawn needs to be true for this to work").getString();
        maxYNether = config.get(FEATURES_NETHER, "Max Y", 100, "The max y a player can go to (everything above is death zone, 0 disables it)").getInt();
        deathZoneTimer = config.get(FEATURES_NETHER, "Death Zone", 60, "The time in which the player will die in the death zone, in seconds").getInt();
        showWarningOnSec = config.get(FEATURES_NETHER, "Death Zone Warning", "60,30,10,9,8,7,6,5,4,3,2,1", "The time when the death zone warning should display when counting down (make it blank so its disabled or 0 to make it display on each second)").getString();

        useVanillaEndPortal = config.get(FEATURES_END, "Use Vanilla Portal", false, "Leave the vanilla portal as it is and don't disable it").getBoolean();

        startingItems = config.getStringList("Starting Items", FIRST_SPAWN, DEFAULT_ITEMS, "The List of Starting Items");
        startingEffects = config.getStringList("Starting Effects", FIRST_SPAWN, DEFAULT_EFFECTS, "The List of Starting Effects");

        generateHellWood = config.get(GENERATION, "Generate Hellwood", true, "Enable the generation of hellwood tree").getBoolean();
        generateAshWood = config.get(GENERATION, "Generate Ashwood", true, "Enable the generation of ashwood tree").getBoolean();
        generateSoulWood = config.get(GENERATION, "Generate Soulwood", true, "Enable the generation of soulwood tree").getBoolean();

        minTankWater = config.get(FEATURES_TANK, "Min Tank Water", 3000, "The minimal amount of water in mB required for the fluid tank to turn into a block that hydrates land (0 to disable)", 0, 4000).getInt();
        tankKeepContent = config.get(FEATURES_TANK, "Keep Fluid", true, "Keep the fluid when tank is broken and put it in the block when placed again").getBoolean();

        portalDebugTool = config.get(FEATURES_PORTAL, "Debug Tool", false, "Enable the debug tool to see additional portal data and amount of master portals in the world (Prints a message in chat when right clicking a portal)").getBoolean();

        netherPortalItems = config.getStringList("Items", FEATURES_NETHER_PORTAL, DEFAULT_NETHER_PORTAL_ITEMS,"The items required to turn on the portal");
        netherPortalFuel = config.get(FEATURES_NETHER_PORTAL, "Fuel","water", "Liquid which is required for the portal to run").getString();
        netherPortalCapacity = config.get(FEATURES_NETHER_PORTAL, "Capacity", 5000, "Capacity of the portal in mB").getInt();
        netherPortalFuelUsage = config.get(FEATURES_NETHER_PORTAL, "Usage", 10, "The time after which the portal will use 1 mB of fuel (seconds)").getInt();
        netherPortalActivationFuelUsage = config.get(FEATURES_NETHER_PORTAL, "Activation Usage", 15, "The amount of fuel the portal will use each tick when activating (1 second = 20 ticks)").getInt();
        netherPortalKeepFluid = config.get(FEATURES_NETHER_PORTAL, "Keep Fluid", true, "Keep the fluid when portal is broken and put it in the block when placed again").getBoolean();
        netherPortalKeepInventory = config.get(FEATURES_NETHER_PORTAL, "Keep Inventory", true, "Keep the inventory when portal is broken and put it in the block when placed again").getBoolean();
        netherPortalPedestalItem = config.get(FEATURES_NETHER_PORTAL, "Pedestal Item", "minecraft:water_bucket", "The item which needs to be on four pedestals around the portal").getString();

        endPortalItems = config.getStringList("Items", FEATURES_END_PORTAL, DEFAULT_END_PORTAL_ITEMS,"The items required to turn on the portal");
        endPortalFuel = config.get(FEATURES_END_PORTAL, "Fuel","water", "Liquid which is required for the portal to run").getString();
        endPortalCapacity = config.get(FEATURES_END_PORTAL, "Capacity", 5000, "Capacity of the portal in mB").getInt();
        endPortalFuelUsage = config.get(FEATURES_END_PORTAL, "Usage", 10, "The time after which the portal will use 1 mB of fuel (seconds)").getInt();
        endPortalActivationFuelUsage = config.get(FEATURES_END_PORTAL, "Activation Usage", 15, "The amount of fuel the portal will use each tick when activating (1 second = 20 ticks)").getInt();
        endPortalKeepFluid = config.get(FEATURES_END_PORTAL, "Keep Fluid", true, "Keep the fluid when portal is broken and put it in the block when placed again").getBoolean();
        endPortalKeepInventory = config.get(FEATURES_END_PORTAL, "Keep Inventory", true, "Keep the inventory when portal is broken and put it in the block when placed again").getBoolean();
        endPortalPedestalItem = config.get(FEATURES_END_PORTAL, "Pedestal Item", "minecraft:water_bucket", "The item which needs to be on four pedestals around the portal").getString();

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
    private final static String[] DEFAULT_NETHER_PORTAL_ITEMS = {
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl",
            "minecraft:blaze_rod, minecraft:enchanted_book, minecraft:blaze_rod",
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl"
    };
    private final static String[] DEFAULT_END_PORTAL_ITEMS = {
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl",
            "minecraft:blaze_rod, minecraft:enchanted_book, minecraft:blaze_rod",
            "minecraft:ender_pearl, minecraft:ender_eye, minecraft:ender_pearl"
    };
}
