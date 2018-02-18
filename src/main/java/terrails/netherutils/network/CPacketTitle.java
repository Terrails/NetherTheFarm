package terrails.netherutils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.capabilities.IDeathZone;
import terrails.netherutils.config.ConfigHandler;

import java.math.BigDecimal;

public class CPacketTitle implements IMessage {

    private int deathCounter;

    public CPacketTitle() {}

    public CPacketTitle(int deathCounter) {
        this.deathCounter = deathCounter;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.deathCounter = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.deathCounter);
    }

    public static class Handler implements IMessageHandler<CPacketTitle, IMessage> {

        @Override
        public IMessage onMessage(CPacketTitle message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = NetherUtils.proxy.getPlayer();
                if (player != null) {

                    Minecraft minecraft = Minecraft.getMinecraft();

                    int minutes = message.deathCounter / 60;
                    int seconds = message.deathCounter % 60;
                    String displayString = (minutes != 0 ? minutes + (minutes == 1 ? " minute" : " minutes") + (seconds != 0 ? " and " + seconds + (seconds == 1 ? " second" : " seconds") : "") : seconds + (seconds == 1 ? " second" : " seconds"));

                    if (message.deathCounter != -1) {
                        minecraft.ingameGUI.displayTitle("DEATH ZONE!!!", null, 1, 1, 1);
                        minecraft.ingameGUI.displayTitle(null, displayString + " until death!", 1, 1, 1);
                    } else {
                        minecraft.ingameGUI.displayTitle(null, null, -1, -1, -1);
                    }
                }
            });

            return null;
        }
    }
}
