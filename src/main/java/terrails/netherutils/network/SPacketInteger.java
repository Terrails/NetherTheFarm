package terrails.netherutils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;

public class SPacketInteger implements IMessage {

    private int value;
    private int x;
    private int y;
    private int z;
    private int option;

    public SPacketInteger() {}

    public SPacketInteger(int value, BlockPos pos) {
        this(value, pos, 1);
    }

    public SPacketInteger(int value, BlockPos pos, int option) {
        this.value = value;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.option = option;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.value = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.option = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.value);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.option);
    }

    public static class Handler implements IMessageHandler<SPacketInteger, IMessage> {

        @Override
        public IMessage onMessage(SPacketInteger message, MessageContext ctx) {
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
                        case 1: // Send fuel amount to server and than to all players in the same dimension
                            if (portal.getTank().getFluid() != null) {
                                portal.getTank().setFluid(new FluidStack(portal.getTank().getFluid(), message.value));
                            } else portal.getTank().setFluid(null);
                            break;
                    }
                } else if (tileEntity instanceof terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) {
                    terrails.netherutils.blocks.portal.end.TileEntityPortalMaster portal = (terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) tileEntity;
                    switch (message.option) {
                        case 1: // Send fuel amount to server and than to all players in the same dimension
                            if (portal.getTank().getFluid() != null) {
                                portal.getTank().setFluid(new FluidStack(portal.getTank().getFluid(), message.value));
                            } else portal.getTank().setFluid(null);
                            break;
                    }
                }
            });
            return null;
        }
    }
}