package terrails.netherthefarm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import terrails.netherthefarm.init.ModBiomes;
import terrails.netherthefarm.init.ModBlocks;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.init.ModItems;
import terrails.netherthefarm.proxies.IProxy;
import terrails.netherthefarm.world.WorldTypeNetherSurvival;

@Mod(modid = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.MOD_VERSION,
        acceptedMinecraftVersions = Constants.MINECRAFT_VERSION,
        guiFactory = Constants.GUI_FACTORY,
        dependencies = "required-after:terracore@[" + Constants.TERRACORE_VERSION + ",);")
public class NetherTheFarm {
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.SERVER_PROXY)
    public static IProxy proxy;
    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        ConfigHandler.init(event.getModConfigurationDirectory());

        ModBlocks.init();
        ModItems.init();
        ModBiomes.init();
        CapabilityFirstSpawn.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        Constants.NETHER_SURVIVAL = new WorldTypeNetherSurvival();
    }

}
