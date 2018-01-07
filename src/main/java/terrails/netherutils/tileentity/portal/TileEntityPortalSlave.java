package terrails.netherutils.tileentity.portal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketBoolean;
import terrails.netherutils.network.SPacketBoolean;
import terrails.netherutils.world.TeleporterNTF;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.block.tile.TileEntityBase;

import java.util.List;

public class TileEntityPortalSlave extends TileEntityBase implements ITickable, IPortalSlave {
    
    private boolean oldActive;
    private boolean status;
    private BlockPos masterPos = BlockPos.ORIGIN;

    public Counter counterCircle = new Counter();
    public Counter counterTeleport = new Counter();
    public boolean isAtPosTopMiddleCircle;
    public boolean isAtPosSideCircles;

    public boolean isReadyToTeleport;


    @Override
    public void update() {
        if (!getWorld().isRemote) {
            boolean hasMaster = false;

            if (!CustomWorldData.get(getWorld()).hasRead()) {
                hasMaster = true;
            }

            if (PortalRegistry.LIST.size() > 0) {
                for (IPortalMaster master : PortalRegistry.LIST) {
                    if (master.getSlavePos().equals(getPos())) {
                        hasMaster = true;
                        isActive(master.isActive());
                        if (isActive()) {
                            doTeleportation();
                        }
                        onChanged();
                    }
                }
            }

            if (!hasMaster) {
                world.setBlockToAir(getPos());
                world.removeTileEntity(getPos());
            }
        }
    }

    private void onChanged() {
        if (oldActive != status) {
            if (!getWorld().isRemote) {
                sendStatus();
                oldActive = status;
            }
        }
    }

    private void doTeleportation() {
        if (getWorld().provider.getDimension() != 0 || getWorld().isRemote)
            return;

        EntityPlayer player = getWorld().getClosestPlayer(getPos().getX() + 0.5, getPos().getY() + 0.75, getPos().getZ() + 0.5, 0.5, false);
        if (player instanceof EntityPlayerMP && isReadyToTeleport) {

            TeleporterNTF.teleport((EntityPlayerMP) player, -1, getMasterPos().add(1, 0, 0), false);

            this.isReadyToTeleport = false;
            sendReadyToTeleport();
        }
    }

    public void sendReadyToTeleport() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isReadyToTeleport, getPos(), 2), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isReadyToTeleport, getPos(), 2));
        }
    }
    public void sendStatus() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActive(), getPos(), 1), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isActive(), getPos(), 1));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isActive(compound.getBoolean("Active"));
        setMasterPos(new BlockPos(compound.getInteger("xMaster"), compound.getInteger("yMaster"), compound.getInteger("zMaster")));
        super.readFromNBT(compound);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("Active", isActive());
        compound.setInteger("xMaster", getMasterPos().getX());
        compound.setInteger("yMaster", getMasterPos().getY());
        compound.setInteger("zMaster", getMasterPos().getZ());
        return super.writeToNBT(compound);
    }

    public void isActive(boolean isActive) {
        this.status = isActive;
    }

    public boolean isActive() {
        return this.status;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.getPos();
    }

    @Override
    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
    }

    @Override
    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-5, -5, -5), getPos().add(5, 5, 5));
    }
}