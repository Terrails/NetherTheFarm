package terrails.netherutils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalSlave;

public class CPacketBoolean implements IMessage {

    private boolean value;
    private int x;
    private int y;
    private int z;
    private int option;

    public CPacketBoolean() {}

    public CPacketBoolean(boolean value, BlockPos pos) {
        this(value, pos, 1);
    }

    public CPacketBoolean(boolean value, BlockPos pos, int option) {
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

    public static class Handler implements IMessageHandler<CPacketBoolean, IMessage> {

        @Override
        public IMessage onMessage(CPacketBoolean message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                return null;

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = NetherUtils.proxy.getPlayer();
                if (player != null) {
                    World world = player.getEntityWorld();
                    TileEntity tileEntity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));

                    if (tileEntity instanceof TileEntityPortalMaster) {
                        TileEntityPortalMaster portal = (TileEntityPortalMaster) tileEntity;
                        switch (message.option) {
                            case 1:
                                portal.isActive(message.value);
                                break;
                            case 2:
                                portal.isActivating = message.value;
                                break;
                            case 3:
                                portal.isActivationDone = message.value;
                                break;
                            case 4:
                                portal.isReadyToTeleport = message.value;
                                break;
                        }
                    } else if (tileEntity instanceof terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) {
                        terrails.netherutils.blocks.portal.end.TileEntityPortalMaster portal = (terrails.netherutils.blocks.portal.end.TileEntityPortalMaster) tileEntity;
                        switch (message.option) {
                            case 1:
                                portal.isActive(message.value);
                                break;
                            case 2:
                                portal.isActivating = message.value;
                                break;
                            case 3:
                                portal.isActivationDone = message.value;
                                break;
                            case 4:
                                portal.isReadyToTeleport = message.value;
                                break;
                        }
                    } else if (tileEntity instanceof TileEntityPortalSlave) {
                        TileEntityPortalSlave portal = (TileEntityPortalSlave) tileEntity;
                        switch (message.option) {
                            case 1:
                                portal.isActive(message.value);
                                break;
                            case 2:
                                portal.isReadyToTeleport = message.value;
                                break;
                        }
                    } else if (tileEntity instanceof terrails.netherutils.blocks.portal.end.TileEntityPortalSlave) {
                        terrails.netherutils.blocks.portal.end.TileEntityPortalSlave portal = (terrails.netherutils.blocks.portal.end.TileEntityPortalSlave) tileEntity;
                        switch (message.option) {
                            case 1:
                                portal.isActive(message.value);
                                break;
                            case 2:
                                portal.isReadyToTeleport = message.value;
                                break;
                        }
                    }
                }
            });

            return null;
        }
    }
}
