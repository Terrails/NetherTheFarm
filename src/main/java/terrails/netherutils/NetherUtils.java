package terrails.netherutils;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.proxies.IProxy;
import terrails.netherutils.world.data.CustomWorldData;

@Mod(modid = Constants.MOD_ID,
        name = Constants.NAME,
        version = Constants.VERSION,
        acceptedMinecraftVersions = Constants.MINECRAFT_VERSION,
        guiFactory = Constants.GUI_FACTORY,
        dependencies = "required-after:terracore@[" + Constants.TERRACORE_VERSION + ",);")
public class NetherUtils {
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.Instance(Constants.MOD_ID)
    public static NetherUtils INSTANCE;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        ModFeatures.init(event);

        ModFeatures.initRegistry();

        ModFeatures.initCapabilities();
        ModFeatures.initEvents();

        ModFeatures.initNetwork();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        ModFeatures.initWorlds();
    }

    @Mod.EventHandler
    public void worldUnload(FMLServerStoppingEvent event) {
        IWorldData data = CustomWorldData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
        if (data != null) {
            data.hasRead(false);
        }
    }

    @Mod.EventHandler()
    public void startedServer(FMLServerStartedEvent event) {
        IWorldData data = CustomWorldData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
        if (data != null) {
            data.hasRead(true);
        }
    }
}
