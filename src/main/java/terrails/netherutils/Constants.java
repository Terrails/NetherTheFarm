package terrails.netherutils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static class Log {
        public static Logger getLogger(String name) {
            return LogManager.getLogger(name);
        }
        public static Logger getLogger() {
            return LogManager.getLogger(NetherUtils.NAME);
        }
        public static void playerMessage(EntityPlayer player, String message) {
            player.sendMessage(new TextComponentString("[" + TextFormatting.RED + "NetherUtils" + TextFormatting.RESET + "] " + message));
        }
    }
}