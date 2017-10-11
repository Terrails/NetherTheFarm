package terrails.netherthefarm;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.netherthefarm.tab.CreativeTabNTF;
import terrails.netherthefarm.world.WorldTypeNetherSurvival;

public class Constants {

    public static final String MOD_ID = "netherthefarm";
    public static final String MOD_NAME = "Nether The Farm";
    public static final String MOD_VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final String TERRACORE_VERSION = "2.1.0";

    public static final CreativeTabs NTF_TAB = new CreativeTabNTF("NetherTheFarm");
    public static WorldType NETHER_SURVIVAL;

    public static final String MINECRAFT_VERSION = "[1.12],[1.12.1],[1.12.2]";

    public static final String CLIENT_PROXY = "terrails.netherthefarm.proxies.ClientProxy";
    public static final String SERVER_PROXY = "terrails.netherthefarm.proxies.ServerProxy";
    public static final String GUI_FACTORY = "terrails.netherthefarm.config.ConfigFactoryGUI";


    public static void playerMessage(EntityPlayer player, String message) {
        player.sendMessage(new TextComponentString("[" + TextFormatting.RED + "NetherTheFarm" + TextFormatting.RESET + "] " + message));
    }
}
