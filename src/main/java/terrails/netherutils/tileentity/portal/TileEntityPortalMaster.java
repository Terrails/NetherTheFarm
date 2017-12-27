package terrails.netherutils.tileentity.portal;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.blocks.BlockPortal;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketBoolean;
import terrails.netherutils.network.CPacketInteger;
import terrails.netherutils.network.SPacketButton;
import terrails.netherutils.world.TeleporterNTF;
import terrails.terracore.block.tile.TileEntityBase;
import terrails.terracore.block.tile.fluid.FluidTankCustom;
import terrails.terracore.helper.BlockHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class TileEntityPortalMaster extends TileEntityBase implements ITickable, IPortalMaster {

    public ItemStackHandler inventory;
    private FluidTank tank;

    public boolean isActivating;
    private int oldFuel;
    private boolean oldActive;

    private boolean status;
    private BlockPos slavePos = BlockPos.ORIGIN;

    public float counterActivation;
    public float counterCircle;
    public boolean isAtPosition2;
    public float counterTeleport;
    public boolean isAtPosition;

    public TileEntityPortalMaster() {
        this.tank = new FluidTankCustom(this, ConfigHandler.portalCapacity) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                if (!ConfigHandler.portalFuel.isEmpty()) {
                    if (fluid.getFluid().getName().toLowerCase().equals(ConfigHandler.portalFuel.toLowerCase())) {
                        return super.canFillFluidType(fluid);
                    }
                }
                return false;
            }
        };
        this.inventory = new ItemStackHandler(11) {
            @Override
            protected void onContentsChanged(int slot) {
                TileEntityPortalMaster.this.markDirty();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!(PortalId.getItemForSlot(slot, stack).getItem().equals(stack.getItem())))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private int counter;
    @Override
    public void update() {
        if (!getWorld().isRemote) {

            if (isActive()) {
                if (counter >= (ConfigHandler.portalFuelUsage * 20)) {
                    setFuel(new FluidStack(getFuel(), getFuelAmount() - 1));
                    counter = 0;
                } else counter++;

                if (!getSlavePos().equals(BlockPos.ORIGIN)) {
                    World theWorld = DimensionManager.getWorld(0);
                    if (!(theWorld.getTileEntity(getSlavePos()) instanceof TileEntityPortalSlave)) {
                        theWorld.setBlockState(getSlavePos(), ModBlocks.PORTAL_SLAVE.getDefaultState());
                        IPortalSlave slave = (IPortalSlave) theWorld.getTileEntity(getSlavePos());
                        if (slave != null) {
                            slave.setMasterPos(getPos());
                        }
                        IBlockState state = Blocks.STONE_SLAB.getDefaultState();
                        theWorld.setBlockState(getSlavePos().east().north(), state);
                        theWorld.setBlockState(getSlavePos().south().east(), state);
                        theWorld.setBlockState(getSlavePos().west().south(), state);
                        theWorld.setBlockState(getSlavePos().west().north(), state);

                    }
                }
                teleport();
            }
            if (!hasFuel() || !hasRequiredBlocks() || !isInvFull()) {
                if (isActive()) {
                    isActive(false);
                }
                if (!hasFuel()) {
                    setFuel(null);
                }
                if (isActivating) {
                    isActivating = false;
                    sendActivating();
                }
            }
            if (!isActive() && !isActivating) {
                counter = 0;
            }

            if (isActivating) {
                setFuel(new FluidStack(getFuel().getFluid(), getFuelAmount() - 15));
                if (counter > 260) {
                    getWorld().createExplosion(null, getPos().getX() + 0.5, getPos().up().getY(), getPos().getZ() + 0.5, 1.0F, false);
                    getWorld().playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.5F, 1.0F);
                    getWorld().playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.BLOCKS, 4.0F, 1.0F);
                    isActive(true);
                    isActivating = false;
                    sendClientPackets();
                    counter = 0;
                } else counter++;
            }

            PortalId.addPortal(this);

            updateFuelItem(getStack(0));
            onChanged();
        }
    }

    private int tpCounter;
    private void teleport() {
        if (getWorld().provider.getDimension() != -1)
            return;
        if (!getWorld().isRemote && getWorld().provider.getDimension() != 0) {
            if (getWorld().isAnyPlayerWithinRangeAt(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5, 0.5)) {
                EntityPlayer player = getWorld().getClosestPlayer(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5, 0.5, false);
                if (player != null && tpCounter >= 230 && player instanceof EntityPlayerMP) {
                    if (getSlavePos().equals(BlockPos.ORIGIN)) generatePortal();
                    TeleporterNTF.teleport((EntityPlayerMP) player, 0, getSlavePos(), false);
                    player.setPositionAndUpdate(getSlavePos().getX() + 1.5, getSlavePos().getY(), getSlavePos().getZ() + 0.5);
                    tpCounter = 0;
                }
                tpCounter++;
            } else tpCounter = 0;
        }
    }
    private void generatePortal() {
        World theWorld = DimensionManager.getWorld(0);
        if (theWorld != null && theWorld.getBlockState(getSlavePos()).getBlock() != ModBlocks.PORTAL_SLAVE) {
            Random random = new Random();
            int xPos = (getPos().getX()) + random.nextInt(101);
            int yPos = (getPos().getY());
            int zPos = (getPos().getZ()) + random.nextInt(101);
            BlockPos firstTopBlock = theWorld.getTopSolidOrLiquidBlock(new BlockPos(xPos, yPos, zPos));

            if (theWorld.getBlockState(firstTopBlock).getMaterial().isLiquid()) {
                for (int i = 256; i > firstTopBlock.getY(); i--) {
                    BlockPos pos = new BlockPos(firstTopBlock.getX(), i, firstTopBlock.getZ());
                    if (BlockHelper.checkAny(5, pos, theWorld, ModBlocks.PORTAL_SLAVE.getDefaultState(), false, true)) firstTopBlock.add(20, 0, 20);
                    if (theWorld.getBlockState(pos).getBlock() != Blocks.AIR) {
                        firstTopBlock = new BlockPos(firstTopBlock.getX(), i, firstTopBlock.getZ());
                        break;
                    }
                }
            }
            BlockHelper.fill(2, firstTopBlock.up(), theWorld, Blocks.AIR.getDefaultState(), false, true);
            BlockHelper.fill(2, firstTopBlock.up().up(), theWorld, Blocks.AIR.getDefaultState(), false, true);
            BlockHelper.fill(2, firstTopBlock, theWorld, Blocks.STONEBRICK.getDefaultState(),false, true);

            theWorld.setBlockState(firstTopBlock.up(), ModBlocks.PORTAL_SLAVE.getDefaultState());
            TileEntity te = theWorld.getTileEntity(firstTopBlock.up());

            if (te != null && te instanceof TileEntityPortalSlave) {
                ((TileEntityPortalSlave) te).setMasterPos(getPos());
                ((TileEntityPortalSlave) te).isActive(true);
                setSlavePos(firstTopBlock.up());
            }

            IBlockState state = Blocks.STONE_SLAB.getDefaultState();
            theWorld.setBlockState(firstTopBlock.up().east().north(), state);
            theWorld.setBlockState(firstTopBlock.up().south().east(), state);
            theWorld.setBlockState(firstTopBlock.up().west().south(), state);
            theWorld.setBlockState(firstTopBlock.up().west().north(), state);
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

    public int transferring;
    private int oldTransferring;
    private void updateFuelItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
            if (handler == null) return;

            if (transferring > 40) {
                FluidStack drainedStack = handler.drain(Math.min((getTankCapacity() - getFuelAmount()), Fluid.BUCKET_VOLUME), true);
                IFluidTankProperties[] tankProperties = handler.getTankProperties();
                if (tankProperties == null || tankProperties.length < 1) return;

                if (drainedStack != null) {
                    tank.fill(drainedStack, true);
                    if (tankProperties[0].getContents() == null) {
                        ItemStack itemStack = inventory.insertItem(1, handler.getContainer(), false);
                        if (itemStack.equals(ItemStack.EMPTY)) {
                            inventory.extractItem(0, handler.getContainer().getCount(), false);
                        } else inventory.setStackInSlot(0, handler.getContainer());
                        if (getStack(0).getCount() <= 0) {
                            inventory.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    }
                }
                transferring = 0;
            } else transferring++;
        }
        if (getFuelAmount() == getTankCapacity() || stack.isEmpty())
            transferring = -1;

        if (oldTransferring != transferring) {
            oldTransferring = transferring;
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketInteger(transferring, getPos(), 1), getWorld().provider.getDimension());
        }
    }
    public boolean isInvEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public boolean isInvFull() {
        for (int i = 2; i < inventory.getSlots(); i++) {
            if (getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    private ItemStack getStack(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public void onChanged() {
        if (oldFuel != getFuelAmount()) {
            sendFuel();
            oldFuel = getFuelAmount();
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.scheduleBlockUpdate(pos, this.getBlockType(),0,0);
            markDirty();
        } else if (oldActive != status) {
            sendActive();
            oldActive = status;
            PortalId.removePortal(this);
        }
    }

    public void sendClientPackets() {
        if (!getWorld().isRemote) {
            sendActive();
            sendActivating();
            sendFuel();
        }
    }
    public void sendServerPackets() {
        if (getWorld().isRemote) {
            sendActive();
            sendActivating();
        }
    }
    public void sendActive() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActive(), getPos(), 0), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketButton(isActive(), getPos(), 0));
        }
    }
    public void sendActivating() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActivating, getPos(), 1), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketButton(isActivating, getPos(), 1));
        }
    }
    public void sendFuel() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketInteger(getFuelAmount(), getPos(), 0), getWorld().provider.getDimension());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        tank = new FluidTankCustom(this, getTankCapacity()).readFromNBT(compound);
        isActive(compound.getBoolean("Active"));
        setSlavePos(new BlockPos(compound.getInteger("xSlave"), compound.getInteger("ySlave"), compound.getInteger("zSlave")));
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        super.readFromNBT(compound);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        tank.writeToNBT(compound);
        compound.setBoolean("Active", isActive());
        compound.setInteger("xSlave", getSlavePos().getX());
        compound.setInteger("ySlave", getSlavePos().getY());
        compound.setInteger("zSlave", getSlavePos().getZ());
        compound.setTag("Inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    public boolean hasFuel() {
        return (getFuelAmount() > 0);
    }
    public int getTankCapacity() {
        return getTank().getCapacity();
    }

  //  @Override
    public int getFuelAmount() {
        return (getFuel() != null ? getFuel().amount : 0);
    }

  //  @Override
    public void setFuel(FluidStack fluidStack) {
        if (fluidStack == null) {
            this.tank.setFluid(null);
        } else this.tank.setFluid(fluidStack);
    }

  //  @Override
    public FluidStack getFuel() {
        return this.tank.getFluid();
    }

    public FluidTank getTank() {
        return this.tank;
    }

    @Override
    public void setSlavePos(BlockPos pos) {
        this.slavePos = pos;
    }

    @Override
    public BlockPos getSlavePos() {
        return this.slavePos;
    }

    @Override
    public void isActive(boolean isActive) {
        this.status = isActive;
    }

    @Override
    public boolean isActive() {
        return this.status;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank) : super.getCapability(capability, facing);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-5, -5, -5), getPos().add(5, 5, 5));
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(getPos().add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
}