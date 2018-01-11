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
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalSlave;

public class SPacketBoolean implements IMessage {

    private boolean value;
    private int x;
    private int y;
    private int z;
    private int option;

    public SPacketBoolean() {}

    public SPacketBoolean(boolean value, BlockPos pos) {
        this(value, pos, 1);
    }

    public SPacketBoolean(boolean value, BlockPos pos, int option) {
        this.value = value;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
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

    public static class Handler implements IMessageHandler<SPacketBoolean, IMessage> {

        @Override
        public IMessage onMessage(SPacketBoolean message, MessageContext ctx) {
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
                        case 1: // Send status to server and than to all players in the same dimension
                            portal.isActive(message.value);
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos, 1), world.provider.getDimension());
                            break;
                        case 2: // Send is activating to server
                            portal.isActivating = message.value;
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos, 2), world.provider.getDimension());
                            break;
                        case 3: // Send is activation done to server
                            portal.isActivationDone = message.value;
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos, 3), world.provider.getDimension());
                            break;
                        case 4: // Send is ready to teleport to server
                            portal.isReadyToTeleport = message.value;
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos, 4), world.provider.getDimension());
                            break;
                    }
                } else if (tileEntity instanceof TileEntityPortalSlave) {
                    TileEntityPortalSlave portal = (TileEntityPortalSlave) tileEntity;
                    switch (message.option) {
                        case 1: // Send status to server and than to all players in the same dimension
                            portal.isActive(message.value);
                            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(message.value, pos, 1), world.provider.getDimension());
                            break;
                        case 2: // Send is ready to teleport to server
                            portal.isReadyToTeleport = message.value;
                            break;
                    }
                }
            });
            return null;
        }
    }
}