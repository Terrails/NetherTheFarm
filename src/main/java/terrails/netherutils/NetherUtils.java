package terrails.netherutils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.init.ModItems;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.base.MainModClass;
import terrails.terracore.base.proxies.ProxyBase;
import terrails.terracore.base.registry.RegistryList;

@Mod(modid = NetherUtils.MOD_ID,
        name = NetherUtils.NAME,
        version = NetherUtils.VERSION,
        guiFactory = NetherUtils.GUI_FACTORY,
        dependencies = "required-after:terracore@[0.0.0,);after:biomesoplenty")
public class NetherUtils extends MainModClass<NetherUtils> {

    public static final String MOD_ID = "netherutils";
    public static final String NAME = "NetherUtils";
    public static final String VERSION = "@VERSION@";
    public static final String GUI_FACTORY = "terrails.netherutils.config.ConfigFactoryGUI";

    public static CreativeTabs TAB_NETHER_UTILS = new CreativeTabs(MOD_ID) {
      @Override
      public ItemStack getTabIconItem() {
          return new ItemStack(ModBlocks.OBELISK);
      }
  };
    public static ProxyBase proxy;

    @Mod.Instance(NetherUtils.MOD_ID)
    public static NetherUtils INSTANCE;

    public NetherUtils() {
        super(MOD_ID, NAME, VERSION);
        NetherUtils.proxy = getProxy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerForgeEntries(RegistryList list) {
        switch (list.getType()) {
            case BLOCK:
                list.addAll(ModBlocks.blocks);
                break;
            case ITEM:
                list.addAll(ModItems.items);
                break;
        }
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModFeatures.init(event);
        ModFeatures.initRegistry();
        ModFeatures.initCapabilities();
        ModFeatures.initEvents();
        ModFeatures.initNetwork();
    }

    @Mod.EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ModFeatures.initWorlds();
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        IWorldData data = CustomWorldData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
        if (data != null) {
            data.hasRead(false);
        }
    }

    @Mod.EventHandler()
    public void serverStarted(FMLServerStartedEvent event) {
        IWorldData data = CustomWorldData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
        if (data != null) {
            data.hasRead(true);
        }
    }

    @Override
    public NetherUtils getInstance() {
        return this;
    }
}
