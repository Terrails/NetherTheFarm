package terrails.netherthefarm.tile;

import codechicken.lib.fluid.FluidUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TileEntityTank extends terrails.terracore.block.tile.fluid.TileEntityTank {

    public TileEntityTank() {
        super(4 * Fluid.BUCKET_VOLUME, true);
    }

    public Material getMaterial() {
        TileEntity tileEntity = world.getTileEntity(getPos());
        if (tileEntity instanceof TileEntityTank) {
            TileEntityTank tankTile = (TileEntityTank) tileEntity;
            if (tankTile.getTank().getFluid() == new FluidStack(FluidRegistry.WATER, getTank().getCapacity())) {
                return Material.WATER;
            }
        }
        return Material.IRON;
    }

    //extends TileEntity implements ITickable {
/*
    private static final int CAPACITY = 4 * Fluid.BUCKET_VOLUME;
    private FluidTank TANK = new FluidTankWithTile(this, CAPACITY);
    public boolean shouldUpdate = false;
    private int oldFluidAmount;
    private FluidStack oldFluidStack;

    public int getLightLevel() {
        if (getTank().getFluid() != null && getTank().getFluidAmount() > 0) {
            return getTank().getFluid().getFluid().getLuminosity();
        } else return 0;
    }
    public int comparatorStrength() {
        return 15 * getTank().getFluidAmount() / getTank().getCapacity();
    }

    @Override
    public void update() {
        if (shouldUpdate) {
            sendUpdates();
            shouldUpdate = false;
        }
        if (getTank().getFluid() != null) {
            if (getTank().getFluidAmount() != oldFluidAmount || getTank().getFluid() != oldFluidStack) {
                sendUpdates();
                oldFluidAmount = getTank().getFluidAmount();
                oldFluidStack = getTank().getFluid();
            }
        }
    }

    private void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
        world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
        markDirty();
    }
    private IBlockState getState() {
        return world.getBlockState(pos);
    }

    public FluidTank getTank() {
        return TANK;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readTankData(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeTankData(compound);
        return super.writeToNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    /*
                    @Override
                public int fill(FluidStack resource, boolean doFill) {
                    return getTank().fill(resource, doFill);
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    if (resource == null || !resource.isFluidEqual(getTank().getFluid())) {
                        return null;
                    }
                    return getTank().drain(resource.amount, doDrain);
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain) {
                    return getTank().drain(maxDrain, doDrain);
                }

                @Override
                public IFluidTankProperties[] getTankProperties() {
                    return getTank().getTankProperties();
                }
     */
/*
    public void readTankData(NBTTagCompound compound) {
        TANK = loadTank(compound, this);
    }
    public void writeTankData(NBTTagCompound compund) {
        saveTank(compund, TANK);
    }
    public static FluidTank loadTank(NBTTagCompound tagCompound, TileEntity tileEntity) {
        final FluidTank tank = new FluidTankWithTile(tileEntity, CAPACITY);
        tank.readFromNBT(tagCompound);
        return tank;
    }
    public static void saveTank(NBTTagCompound tagCompound, FluidTank tank) {
        tank.writeToNBT(tagCompound);
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
                    @Override
                    public int fill(FluidStack resource, boolean doFill) {
                        int amount = getTank().fill(resource, doFill);
                        if (resource == null) return 0;
                        if (facing != EnumFacing.UP) return 0;
                        return amount;
                    }

                    @Nullable
                    @Override
                    public FluidStack drain(FluidStack resource, boolean doDrain) {
                        if (resource == null || !resource.isFluidEqual(getTank().getFluid())) {
                            return null;
                        }
                        if (facing != EnumFacing.DOWN) return null;
                        return getTank().drain(resource.amount, doDrain);
                    }

                    @Nullable
                    @Override
                    public FluidStack drain(int maxDrain, boolean doDrain) {
                        if (facing != EnumFacing.DOWN) return null;
                        return getTank().drain(maxDrain, doDrain);
                    }

                    @Override
                    public IFluidTankProperties[] getTankProperties() {
                        return getTank().getTankProperties();
                    }
                });
            }
        }
        return super.getCapability(capability, facing);
    }*/
}
