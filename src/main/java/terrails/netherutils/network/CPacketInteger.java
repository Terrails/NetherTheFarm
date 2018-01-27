package terrails.netherutils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;

public class CPacketInteger implements IMessage {

    private int value;
    private int option;
    private int x;
    private int y;
    private int z;

    public CPacketInteger() {}

    public CPacketInteger(int value, BlockPos pos, int option) {
        this.value = value;
        this.option = option;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public CPacketInteger(int value, BlockPos pos) {
        this(value, pos, 1);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.value = buf.readInt();
        this.option = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.value);
        buf.writeInt(this.option);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    public static class Handler implements IMessageHandler<CPacketInteger, IMessage> {

        @Override
        public IMessage onMessage(CPacketInteger message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = NetherUtils.proxy.getPlayer();
                if (player != null) {
                    World world = player.getEntityWorld();
                    TileEntity tileEntity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));

                    if (tileEntity instanceof TileEntityPortalMaster) {
                        TileEntityPortalMaster tile =(TileEntityPortalMaster) tileEntity;
                        switch (message.option) {
                            case 1:
                                if (tile.getTank().getFluid() != null) {
                                    tile.getTank().setFluid(new FluidStack(tile.getTank().getFluid(), message.value));
                                } else tile.getTank().setFluid(null);
                                break;
                            case 2:
                                tile.counterFluidTransfer.set(message.value);
                                break;
                        }
                    } else if (tileEntity instanceof terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) {
                        terrails.netherutils.blocks.portal.end.TileEntityPortalMaster tile =(terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) tileEntity;
                        switch (message.option) {
                            case 1:
                                if (tile.getTank().getFluid() != null) {
                                    tile.getTank().setFluid(new FluidStack(tile.getTank().getFluid(), message.value));
                                } else tile.getTank().setFluid(null);
                                break;
                            case 2:
                                tile.counterFluidTransfer.set(message.value);
                                break;
                        }
                    }
                }
            });

            return null;
        }
    }
}
