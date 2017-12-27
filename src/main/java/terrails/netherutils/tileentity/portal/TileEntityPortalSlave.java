package terrails.netherutils.tileentity.portal;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketBoolean;
import terrails.netherutils.world.TeleporterNTF;
import terrails.terracore.block.tile.TileEntityBase;

public class TileEntityPortalSlave extends TileEntityBase implements ITickable, IPortalSlave {
    
    private boolean oldActive;
    private boolean status;
    private BlockPos masterPos = BlockPos.ORIGIN;

    public float counterCircle;
    public boolean isAtPosition2;

    public float counterTeleport;
    public boolean isAtPosition;

    @Override
    public void update() {
        if (!getWorld().isRemote) {

            boolean hasIt = false;
            for (IPortalMaster master : PortalId.MASTER_LIST) {
                if (master.getSlavePos().equals(getPos())) {
                    hasIt = true;
                    if (hasRequiredBlocks()) {
                        isActive(master.isActive());
                        if (isActive()) {
                            teleport();
                        } else hasIt = false;
                    } else isActive(false);
                    onChanged();
                }
            }
            if (!hasIt) {
                world.setBlockToAir(getPos());
                world.removeTileEntity(getPos());
            }
        }
    }

    private int tpCounter;
    private void teleport() {
        if (getWorld().provider.getDimension() != 0)
            return;
        if (!getWorld().isRemote && getWorld().provider.getDimension() == 0) {
            if (getWorld().isAnyPlayerWithinRangeAt(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5, 0.5)) {
                EntityPlayer player = getWorld().getClosestPlayer(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5, 0.5, false);
                if (player != null && tpCounter >= 230 && player instanceof EntityPlayerMP) {
                    TeleporterNTF.teleport((EntityPlayerMP) player, -1, getMasterPos(), false);
                    player.setPositionAndUpdate(getMasterPos().getX() + 1.5, getMasterPos().getY(), getMasterPos().getZ() + 0.5);
                    tpCounter = 0;
                }
                tpCounter++;
            } else tpCounter = 0;
        }
    }

    public boolean hasRequiredBlocks() {
        Block block = Blocks.STONE_SLAB;
        if (getWorld().getBlockState(getPos().east().north()).getBlock() == block) {
            if (getWorld().getBlockState(getPos().south().east()).getBlock() == block) {
                if (getWorld().getBlockState(getPos().west().south()).getBlock() == block) {
                    if (getWorld().getBlockState(getPos().west().north()).getBlock() == block) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void onChanged() {
        if (oldActive != status) {
            if (!getWorld().isRemote) {
                ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActive(), getPos(), 0), getWorld().provider.getDimension());
                oldActive = status;
            }
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