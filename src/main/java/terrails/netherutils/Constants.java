package terrails.netherutils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.netherutils.init.ModBlocks;

public class Constants {

    public static final String MOD_ID = "netherutils";
    public static final String NAME = "NetherUtils";
    public static final String VERSION = "1.3.14";

    public static final String MINECRAFT_VERSION = "1.12.2";
    public static final String TERRACORE_VERSION = "2.1.6";

    public static final String CLIENT_PROXY = "terrails.netherutils.proxies.ClientProxy";
    public static final String SERVER_PROXY = "terrails.netherutils.proxies.ServerProxy";
    public static final String GUI_FACTORY = "terrails.netherutils.config.ConfigFactoryGUI";

    public static class CreativeTab {
        public static final CreativeTabs NetherUtils = new CreativeTabs(MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(ModBlocks.OBELISK);
            }
        };
    }

    public static class Log {

        public static Logger getLogger(String name) {
            return LogManager.getLogger(name);
        }

        public static Logger getLogger() {
            return LogManager.getLogger(NAME);
        }

        public static void playerMessage(EntityPlayer player, String message) {
            player.sendMessage(new TextComponentString("[" + TextFormatting.RED + "NetherUtils" + TextFormatting.RESET + "] " + message));
        }
    }
}