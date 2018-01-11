package terrails.netherutils.blocks.tank;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
import terrails.netherutils.api.handler.FluidHandler;
import terrails.netherutils.config.ConfigHandler;
import terrails.terracore.block.tile.TileEntityBase;
import terrails.terracore.block.tile.fluid.FluidTankCustom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityTank extends TileEntityBase implements ITickable {

    public static final int CAPACITY = 4 * Fluid.BUCKET_VOLUME;
    private FluidTank tank = new FluidTankCustom(this, CAPACITY);

    private int oldFluidAmount;
    private FluidStack oldFluidStack;

    @Override
    public void update() {
        IBlockState state = world.getBlockState(pos);
        if (hasWater() && state.getValue(BlockTank.LEVEL) == 0 && !hasLava()) {
            world.setBlockState(pos, state.withProperty(BlockTank.LEVEL, 1), 2);
        } else if ((!hasWater() && state.getValue(BlockTank.LEVEL) > 0) || hasLava()) {
            world.setBlockState(pos, state.withProperty(BlockTank.LEVEL, 0), 2);
        }

        if (getFluidStack() != null) {
            if (getFluidStack().amount != oldFluidAmount || getFluidStack() != oldFluidStack) {
                oldFluidAmount = getFluidStack().amount;
                oldFluidStack = getFluidStack();
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

    // == Custom Methods == \\

    public int getComparatorStrength() {
        return 15 * getTank().getFluidAmount() / getTank().getCapacity();
    }
    private boolean hasWater() {
        return ConfigHandler.minTankWater != 0 && tank.getFluid() != null && tank.getFluid().getFluid() == FluidRegistry.WATER && tank.getFluidAmount() >= ConfigHandler.minTankWater;
    }
    private boolean hasLava() {
        for (EnumFacing facing : EnumFacing.values()) {
            Block block = world.getBlockState(pos.offset(facing)).getBlock();
            if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                return true;
            }
        }
        return false;
    }

    public FluidTank getTank() {
        return tank;
    }
    public void setTank(FluidTank tank) {
        this.tank = tank;
    }

    public FluidStack getFluidStack() {
        return tank.getFluid();
    }

    // == End == \\

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
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
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandler(getTank())) : super.getCapability(capability, facing);
    }
}
