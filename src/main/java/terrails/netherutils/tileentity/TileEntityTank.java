package terrails.netherutils.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import terrails.netherutils.api.handler.FluidHandler;
import terrails.netherutils.blocks.BlockTank;
import terrails.netherutils.config.ConfigHandler;
import terrails.terracore.block.tile.TileEntityBase;
import terrails.terracore.block.tile.fluid.FluidTankCustom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityTank extends TileEntityBase implements ITickable {

    public static final int CAPACITY = 4 * Fluid.BUCKET_VOLUME;
    private FluidTank tank = new FluidTankCustom(this, CAPACITY);

    private int oldFluidAmount;
    private FluidStack oldFluidStack;

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
       return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        IBlockState state = world.getBlockState(pos);
        if (hasWater() && !state.getValue(BlockTank.HAS_WATER)) {
            world.setBlockState(pos, state.withProperty(BlockTank.HAS_WATER, true), 2);
        } else if (!hasWater() && state.getValue(BlockTank.HAS_WATER)) {
            world.setBlockState(pos, state.withProperty(BlockTank.HAS_WATER, false), 2);
        }

        if (getTank().getFluid() != null) {
            if (getTank().getFluidAmount() != oldFluidAmount || getTank().getFluid() != oldFluidStack) {
                oldFluidAmount = getTank().getFluidAmount();
                oldFluidStack = getTank().getFluid();
                sendUpdates();
            }
        }
    }

    private void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        world.scheduleBlockUpdate(pos, this.getBlockType(),0,0);
        markDirty();
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tank = new FluidTankCustom(this, CAPACITY).readFromNBT(compound);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        tank.writeToNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        IFluidHandler fluidHandler = new FluidHandler(getTank());
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);
        }
        return super.getCapability(capability, facing);
    }

    public int getLightLevel() {
        if (getTank().getFluid() != null && getTank().getFluidAmount() > 0) {
            return getTank().getFluid().getFluid().getLuminosity();
        } else return 0;
    }
    public int getComparatorStrength() {
        return 15 * getTank().getFluidAmount() / getTank().getCapacity();
    }
    public boolean hasWater() {
        return ConfigHandler.minTankWater != 0 && tank.getFluid() != null && tank.getFluid().getFluid() == FluidRegistry.WATER && tank.getFluidAmount() >= ConfigHandler.minTankWater;
    }

    public FluidTank getTank() {
        return tank;
    }
    public void setTank(FluidTank tank) {
        this.tank = tank;
    }
}
