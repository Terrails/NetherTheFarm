package terrails.netherutils.init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.entity.capabilities.deathzone.CapabilityDeathZone;
import terrails.netherutils.entity.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherutils.entity.capabilities.obelisk.CapabilityObelisk;
import terrails.netherutils.entity.capabilities.portal.CapabilityPortal;
import terrails.netherutils.event.BlockEvent;
import terrails.netherutils.event.EntityEvent;
import terrails.netherutils.event.RegisterEvent;
import terrails.netherutils.event.SpawnEvent;
import terrails.netherutils.network.*;
import terrails.netherutils.world.nether.TreesGenerator;

public class ModFeatures {

    public static void init(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getModConfigurationDirectory());
    }

    public static void initCapabilities() {
        CapabilityFirstSpawn.register();
        CapabilityObelisk.register();
        CapabilityPortal.register();
        CapabilityDeathZone.register();
    }

    public static void initRegistry() {
        ModBlocks.init();
        ModItems.init();
        ModAdvancements.init();
        MinecraftForge.EVENT_BUS.register(new RegisterEvent());
    }

    public static void initEvents() {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityFirstSpawn.Handler());
        MinecraftForge.EVENT_BUS.register(new CapabilityPortal.Handler());
        MinecraftForge.EVENT_BUS.register(new CapabilityObelisk.Handler());
        MinecraftForge.EVENT_BUS.register(new CapabilityDeathZone.Handler());
        MinecraftForge.EVENT_BUS.register(new EntityEvent());
        MinecraftForge.EVENT_BUS.register(new SpawnEvent());
        MinecraftForge.EVENT_BUS.register(new BlockEvent());
    }

    public static void initNetwork() {
        Network.WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);

        NetworkRegistry.INSTANCE.registerGuiHandler(NetherUtils.INSTANCE, new ModGUIs());
        Network.WRAPPER.registerMessage(SPacketBoolean.Handler.class, SPacketBoolean.class, Network.nextID(), Side.SERVER);
        Network.WRAPPER.registerMessage(SPacketInteger.Handler.class, SPacketInteger.class, Network.nextID(), Side.SERVER);
        Network.WRAPPER.registerMessage(CPacketInteger.Handler.class, CPacketInteger.class, Network.nextID(), Side.CLIENT);
        Network.WRAPPER.registerMessage(CPacketBoolean.Handler.class, CPacketBoolean.class, Network.nextID(), Side.CLIENT);
        Network.WRAPPER.registerMessage(CPacketTitle.Handler.class, CPacketTitle.class, Network.nextID(), Side.CLIENT);
    }

    public static void initWorlds() {
        GameRegistry.registerWorldGenerator(new TreesGenerator(), 0);
    }

    public static class Network {
        private static int packetId = 0;
        public static SimpleNetworkWrapper WRAPPER = null;

        private static int nextID() {
            return packetId++;
        }

    }
}
