package terrails.netherthefarm;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherthefarm.blocks.ModBlocks;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.event.FirstSpawnEvent;
import terrails.netherthefarm.event.NetherEvent;
import terrails.netherthefarm.items.ModItems;
import terrails.netherthefarm.proxies.IProxy;
import terrails.netherthefarm.tab.CreativeTabNTF;
import terrails.netherthefarm.world.WorldTypeNetherSurvival;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    public static CreativeTabs creativeTab = new CreativeTabNTF("NetherTheFarm");

    public NetherEvent netherEvent = new NetherEvent();
    public WorldType Hellworld = new WorldTypeNetherSurvival();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        ModBlocks.init();
        ModItems.init();
        ConfigHandler.init(event.getModConfigurationDirectory());

        CapabilityFirstSpawn.register();

        MinecraftForge.EVENT_BUS.register(netherEvent);
        MinecraftForge.EVENT_BUS.register(new FirstSpawnEvent());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
