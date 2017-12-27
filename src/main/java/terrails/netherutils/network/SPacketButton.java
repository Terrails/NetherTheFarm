package terrails.netherutils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.tileentity.portal.TileEntityPortalMaster;

public class SPacketButton implements IMessage {

    private boolean value;
    private int x;
    private int y;
    private int z;
    private int option;

    public SPacketButton() {}

    public SPacketButton(boolean value, BlockPos pos) {
        this.value = value;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public SPacketButton(boolean value, BlockPos pos, int option) {
        this(value, pos);
        this.option = option;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.value = buf.readBoolean();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.option = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.value);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.option);
    }

    public static class Handler implements IMessageHandler<SPacketButton, IMessage> {

        @Override
        public IMessage onMessage(SPacketButton message, MessageContext ctx) {
            if (ctx.side != Side.SERVER)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                WorldServer world = serverPlayer.getServerWorld();
                BlockPos pos = new BlockPos(message.x, message.y, message.z);

                TileEntity tileEntity = world.getTileEntity(pos);

                if (tileEntity instanceof TileEntityPortalMaster) {
                    TileEntityPortalMaster portal = (TileEntityPortalMaster) tileEntity;
                    switch (message.option) {
                        case 0:
                            portal.isActive(message.value);
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos), world.provider.getDimension());
                            break;
                        case 1:
                            portal.isActivating = message.value;
                            break;
                    }
                } /*else if (tileEntity instanceof TileEntityPortalSlave) {
                    ((TileEntityPortalSlave) tileEntity).isActive(message.value);
                }*/

                /*
                if (tileEntity instanceof TileEntityPortal) {
                    TileEntityPortal portal = (TileEntityPortal) tileEntity;
                    if (message.option == 0) {
                        portal.isActive(value);
                        ModNetwork.NETWORK.sendToDimension(new CPacketBoolean(value, pos), world.provider.getDimension());
                    } else if (message.option == 1) {
                        portal.isActivating(value);
                    }
                } else
                if (tileEntity instanceof TileEntityPortalSlave) {
                    ((TileEntityPortalSlave) tileEntity).isActive(value);
                }
                */
            });
            return null;
        }
    }
}
