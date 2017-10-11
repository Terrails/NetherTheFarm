package terrails.netherthefarm.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;

import java.io.File;

public class ConfigHandler {

    public static Configuration configFile;

    public static final String GENERAL_SETTINGS = "General Settings";

    // Boolean
    public static boolean obelisk;

    // Other
    public static String[] startingItems;
    public static String[] startingPotions;

    @SuppressWarnings("deprecation")
    public static void init(File dir) {
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        configFile = new Configuration(new File(dir, Constants.MOD_ID + ".cfg"));
        syncConfig();
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Constants.MOD_ID)) {
            syncConfig();
        }
    }

    public static void syncConfig(){
        obelisk = configFile.get(GENERAL_SETTINGS, "Spawn Point Respawn", true, "Disable if you don't use Nether Survival World Type and \n want to use Biomes O' Plenty World Type without spawning in nether").getBoolean();

        startingItems = configFile.getStringList("Starting Items", GENERAL_SETTINGS + ".Starting Features", DEFAULT_ITEMS, "The List of Starting Items");
        startingPotions = configFile.getStringList("Starting Effects", GENERAL_SETTINGS + ".Starting Features", DEFAULT_EFFECTS, "The List of Starting Effects");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    private final static String[] EMPTY_STRING = {};

    private final static String[] DEFAULT_ITEMS = {
            "minecraft:stone_pickaxe -enchantment:unbreaking:2 -name:'Start Pick'"
    };
    private final static String[] DEFAULT_EFFECTS = {
            "fire_resistance -time:150"
    };
}
