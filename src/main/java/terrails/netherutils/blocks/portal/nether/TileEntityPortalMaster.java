package terrails.netherutils.blocks.portal.nether;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
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
import terrails.netherutils.blocks.pedestal.BlockPedestal;
import terrails.netherutils.blocks.portal.PortalRegistry;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketBoolean;
import terrails.netherutils.network.CPacketInteger;
import terrails.netherutils.network.SPacketBoolean;
import terrails.netherutils.network.SPacketInteger;
import terrails.netherutils.blocks.pedestal.TileEntityPedestal;
import terrails.netherutils.blocks.portal.Counter;
import terrails.netherutils.world.TeleporterNTF;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.block.tile.TileEntityBase;
import terrails.terracore.block.tile.fluid.FluidTankCustom;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class TileEntityPortalMaster extends TileEntityBase implements ITickable, IPortalMaster {

    public ItemStackHandler inventory;
    private FluidTank tank;

    private boolean status;
    private BlockPos slavePos = BlockPos.ORIGIN;

    private int oldFuel;
    public boolean isActivating;
    private boolean oldActive;
    private Counter counterFuel = new Counter();

    public Counter counterFluidTransfer = new Counter();
    private int oldFluidTransfer;

    //-----------TESR Counters and Booleans-------------\\
    public Counter counterActivation = new Counter();
    public Counter counterCircle = new Counter();
    public Counter counterTeleport = new Counter();
    public Counter counterRotation = new Counter();
    public boolean isAtPosTopMiddleCircle;
    public boolean isAtPosSideCircles;

    public boolean isActivationDone;
    public boolean isReadyToTeleport;
    //--------------------------------------------------\\

    public TileEntityPortalMaster() {
        this.tank = new FluidTankCustom(this, ConfigHandler.netherPortalCapacity) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                if (!ConfigHandler.netherPortalFuel.isEmpty()) {
                    if (fluid.getFluid().getName().toLowerCase().equals(ConfigHandler.netherPortalFuel.toLowerCase())) {
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
                ItemStack slotStack = PortalRegistry.getItemForSlot(ConfigHandler.netherPortalItems, ConfigHandler.netherPortalFuel, slot, stack);
                boolean i = !(slotStack.getItem().equals(stack.getItem()));
                boolean j = (slotStack.getCount() == 2 && slotStack.getMetadata() != stack.getMetadata());
                if (i || j) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public void update() {
        if (getWorld().isRemote) {
            updateRendering();
            return;
        }

        if (isActive()) {
            if (counterFuel.value() >= (ConfigHandler.netherPortalFuelUsage * 20)) {
                setFuel(new FluidStack(getFuel(), getFuelAmount() - 1));
                counterFuel.clear();
            } else counterFuel.increment();

            doTeleportation();

            // If slave coords are set but it doesn't exist create a new one
            if (!getSlavePos().equals(BlockPos.ORIGIN)) {
                World overworld = getSlaveWorld();//DimensionManager.getWorld(0);
                if (overworld != null && !(overworld.getTileEntity(getSlavePos()) instanceof TileEntityPortalSlave)) {
                    overworld.setBlockState(getSlavePos(), ModBlocks.PORTAL_NETHER_SLAVE.getDefaultState());
                    IPortalSlave slave = (IPortalSlave) overworld.getTileEntity(getSlavePos());
                    if (slave != null) {
                        slave.setMasterPos(getPos());
                        slave.setMasterDimension(getDimension());
                    }
                }
            }
        }
        if (!hasFuel() || !hasRequiredBlocks() || !hasRequiredItems()) {
            if (isActive()) {
                isActive(false);
            }
            if (!hasFuel()) {
                setFuel(null);
            }
            if (isActivating) {
                isActivating = false;
                sendActivation();
            }
        }

        if (isActivating) {
            if (isActivationDone) {
                getWorld().createExplosion(null, getPos().getX() + 0.5, getPos().up().getY(), getPos().getZ() + 0.5, 1.0F, false);
                getWorld().playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.5F, 1.0F);
                getWorld().playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.BLOCKS, 4.0F, 1.0F);
                isActive(true);
                isActivating = false;
                isActivationDone = false;
                sendActivation();
                sendActivationDone();
            }
            setFuel(new FluidStack(getFuel().getFluid(), getFuelAmount() - ConfigHandler.netherPortalActivationFuelUsage));
        }
        updateFluidItem();

        if (oldFuel != getFuelAmount()) {
            sendFuel();
            oldFuel = getFuelAmount();
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
            markDirty();
        } else if (oldActive != status) {
            sendActive();
            oldActive = status;
            int index = 0;
            for (IPortalMaster master : PortalRegistry.LIST) {
                if (master.getBlockPos().equals(this.getPos()) && master.getDimension() == getDimension()) {
                    master.isActive(status);
                    PortalRegistry.LIST.set(index, master);
                    CustomWorldData.get(getWorld()).markDirty();
                    break;
                }
                index++;
            }

        }
    }


    // == Packets == \\

    public void sendActive() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActive(), getPos(), 1), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isActive(), getPos(), 1));
        }
    }
    public void sendActivation() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActivating, getPos(), 2), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isActivating, getPos(), 2));
        }
    }
    public void sendActivationDone() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActivationDone, getPos(), 3), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isActivationDone, getPos(), 3));
        }
    }
    public void sendReadyToTeleport() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isReadyToTeleport, getPos(), 4), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isReadyToTeleport, getPos(), 4));
        }
    }
    public void sendFuel() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketInteger(getFuelAmount(), getPos(), 1), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketInteger(getFuelAmount(), getPos(), 1));
        }
    }

    /* == End == */


    /* == IPortalMaster == */

    @Override
    public BlockPos getBlockPos() {
        return this.getPos();
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
    public int getDimension() {
        return world.provider.getDimension();
    }

    @Override
    public boolean isNether() {
        return true;
    }

    /* == End == */


    /* == Basic & Rendering == */

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-5, -5, -5), getPos().add(5, 5, 5));
    }
    private void updateRendering() {
        boolean isPlayerInRange = getWorld().isAnyPlayerWithinRangeAt(this.getPos().getX() + 0.5, this.getPos().getY() + 0.75, this.getPos().getZ() + 0.5, 0.5);
        if (this.isActivating) {
            if (!this.isActivationDone) {
                this.counterActivation.increment(1);

                if (this.counterActivation.value() >= 200) {
                    this.isActivationDone = true;
                    this.sendActivationDone();
                    this.counterActivation.clear();
                }
            }
        } else if (this.isActive()) {
            if (isPlayerInRange) {
                this.counterCircle.increment(1);
                this.counterTeleport.increment(1);

                if (this.counterCircle.value() >= 300) {
                    this.counterCircle.clear();
                }

                if (this.counterTeleport.value() >= 100) {
                    if (this.isAtPosTopMiddleCircle) {
                        this.isReadyToTeleport = true;
                        this.sendReadyToTeleport();
                    }
                    this.isAtPosTopMiddleCircle = true;
                    this.counterTeleport.clear();
                }
            } else {
                this.counterCircle.clear();
                this.counterTeleport.clear();
                this.counterActivation.clear();
                this.isAtPosSideCircles = false;
                this.isAtPosTopMiddleCircle = false;
            }
        } else if (!this.isActive()) {
            this.counterActivation.clear();
            this.counterCircle.clear();
            this.counterTeleport.clear();
            this.isAtPosTopMiddleCircle = false;
            this.isAtPosSideCircles = false;
        }

        if (this.counterRotation.value() >= 360) {
            this.counterRotation.clear();
        } else this.counterRotation.increment();
    }

    private World getSlaveWorld() {
        if (getDimension() == 0) {
            return DimensionManager.getWorld(-1);
        } else return DimensionManager.getWorld(0);
    }

    private void updateFluidItem() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (!stack.isEmpty()) {
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
            IFluidHandlerItem fluidHandlerCopy = FluidUtil.getFluidHandler(stack.copy());

            if (fluidHandler == null || fluidHandler.getTankProperties()[0].getContents() == null) {
                counterFluidTransfer.set(-1);
                return;
            }
            if (fluidHandlerCopy == null || fluidHandlerCopy.getTankProperties()[0].getContents() == null) {
                counterFluidTransfer.set(-1);
                return;
            }

            int toDrain = Math.min(getFuelCapacity() - getFuelAmount(), Fluid.BUCKET_VOLUME);

            if (toDrain <= 0)
                return;


            FluidStack drainedStack = fluidHandler.drain(toDrain, false);
            IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
            if (tankProperties == null || tankProperties.length < 1)
                return;

            if (drainedStack != null) {
                if (counterFluidTransfer.value() > 40) {
                    drainedStack = fluidHandler.drain(toDrain, true);
                    tankProperties = fluidHandler.getTankProperties();
                    tank.fill(drainedStack, true);

                    if (tankProperties[0].getContents() == null) {
                        ItemStack itemStack = inventory.insertItem(1, fluidHandler.getContainer(), false);
                        if (itemStack.equals(ItemStack.EMPTY)) {
                            inventory.extractItem(0, fluidHandler.getContainer().getCount(), false);
                        } else inventory.setStackInSlot(0, fluidHandler.getContainer());
                        if (inventory.getStackInSlot(0).getCount() <= 0) {
                            inventory.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    }
                    counterFluidTransfer.clear();
                } else counterFluidTransfer.increment();
            } else counterFluidTransfer.set(-1);

        }

        if (getFuelAmount() >= getFuelCapacity() || stack.isEmpty()) {
            counterFluidTransfer.set(-1);
        }

        if (oldFluidTransfer != (int) counterFluidTransfer.value()) {
            oldFluidTransfer = (int) counterFluidTransfer.value();
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketInteger((int) counterFluidTransfer.value(), getPos(), 2), getWorld().provider.getDimension());
        }

    }
    private void doTeleportation() {
        if (!getWorld().provider.getDimensionType().getName().equalsIgnoreCase(DimensionType.NETHER.getName()) && !getWorld().provider.getDimensionType().getName().equalsIgnoreCase(DimensionType.OVERWORLD.getName()))
            return;
        if (!getWorld().isRemote) {
            EntityPlayer player = getWorld().getClosestPlayer(getPos().getX() + 0.5, getPos().getY() + 0.75, getPos().getZ() + 0.5, 0.5, false);
            if (player != null && this.isReadyToTeleport && player instanceof EntityPlayerMP) {

                if (getSlavePos().equals(BlockPos.ORIGIN)) {
                    generatePortal();
                }

                String dimName = getWorld().provider.getDimensionType().getName();
                TeleporterNTF.teleport((EntityPlayerMP) player, dimName.equalsIgnoreCase(DimensionType.NETHER.getName()) ? 0 : -1, getSlavePos().add(1, 0, 0), false);

                this.isReadyToTeleport = false;
                ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isReadyToTeleport, getPos(), 4), getWorld().provider.getDimension());
            }
        }
    }
    private void generatePortal() {
       // World overworld = DimensionManager.getWorld(0); // Overworld
        ChunkProviderServer providerServer = null;
        WorldServer worldServerForDimension = null;

        if (getDimension() == 0) {
            MinecraftServer server = getWorld().getMinecraftServer();
            if (server != null) {
                worldServerForDimension = getWorld().getMinecraftServer().getWorld(-1);
                providerServer = worldServerForDimension.getChunkProvider();
                if (!providerServer.chunkExists(0, 0)) {
                    providerServer.loadChunk(0, 0);
                    worldServerForDimension.getBlockState(new BlockPos(1,1,1));
                }
            }
        }

        World slaveWorld = getSlaveWorld();

        if (slaveWorld != null && !(slaveWorld.getTileEntity(getSlavePos()) instanceof TileEntityPortalSlave)) {

            Random random = new Random();
            BlockPos pos = new BlockPos((getPos().getX() * 8) + random.nextInt(201), getPos().getY(), (getPos().getZ() * 8) + random.nextInt(201));

            if (getDimension() == -1) {
                for (int i = slaveWorld.getHeight(); i > 0; i--) {
                    BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
                    IBlockState state = slaveWorld.getBlockState(blockPos);

                    if (state.getBlock() != Blocks.AIR) {
                        // If the pos is near another slave offset your coords and start from world height again
                        if (TeleporterNTF.checkAny(6, blockPos, slaveWorld, ModBlocks.PORTAL_NETHER_SLAVE.getDefaultState(), false, true)) {
                            pos.add(20, 0, 20);
                            i = slaveWorld.getHeight();
                        } else if (!slaveWorld.getBlockState(blockPos).getBlock().isLeaves(state, getWorld(), blockPos) && !slaveWorld.getBlockState(blockPos).getBlock().isFoliage(getWorld(), blockPos)) {
                            pos = new BlockPos(blockPos.getX(), i, blockPos.getZ());
                            break;
                        }
                    }
                }
            } else {
                for (int i = 90; i > 0; i--) {
                    BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
                    IBlockState state = slaveWorld.getBlockState(blockPos);

                    if (state.getBlock() != Blocks.AIR) {
                        // If the pos is near another slave offset your coords and start from world height again
                        if (TeleporterNTF.checkAny(6, blockPos, slaveWorld, ModBlocks.PORTAL_NETHER_SLAVE.getDefaultState(), false, true)) {
                            pos.add(20, 0, 20);
                            i = 90;
                        } else if (!slaveWorld.getBlockState(blockPos).getBlock().isLeaves(state, getWorld(), blockPos) && !slaveWorld.getBlockState(blockPos).getBlock().isFoliage(getWorld(), blockPos)) {
                            pos = new BlockPos(blockPos.getX(), i, blockPos.getZ());
                            break;
                        }
                    }
                }
            }

            TeleporterNTF.fill(2, pos, slaveWorld, Blocks.STONEBRICK.getDefaultState(), false, true);
            TeleporterNTF.fill(2, pos.up(), slaveWorld, Blocks.AIR.getDefaultState(), false, true);
            TeleporterNTF.fill(2, pos.up().up(), slaveWorld, Blocks.AIR.getDefaultState(), false, true);
            TeleporterNTF.fill(2, pos.up().up().up(), slaveWorld, Blocks.AIR.getDefaultState(), false, true);

            slaveWorld.setBlockState(pos.up(), ModBlocks.PORTAL_NETHER_SLAVE.getDefaultState());
            TileEntity te = slaveWorld.getTileEntity(pos.up());

            if (te != null && te instanceof TileEntityPortalSlave) {
                ((TileEntityPortalSlave) te).setMasterPos(getPos());
                ((TileEntityPortalSlave) te).isActive(true);
                ((TileEntityPortalSlave) te).setMasterDimension(getDimension());
                setSlavePos(pos.up());
            }
            int index = 0;
            for (IPortalMaster master : PortalRegistry.LIST) {
                if (master.getBlockPos().equals(this.getPos())) {
                    master.setSlavePos(this.getSlavePos());
                    PortalRegistry.LIST.set(index, master);
                    CustomWorldData.get(getWorld()).markDirty();
                    break;
                }
                index++;
            }
        }

        if (getDimension() == 0 && providerServer != null) {
            providerServer.queueUnload(new Chunk(worldServerForDimension, 0, 0));
        }
    }

    public boolean isInvEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public boolean isInvFull() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasRequiredBlocks() {
        List<BlockPos> positions = Lists.newArrayList(getPos().east().north(), getPos().south().east(), getPos().west().south(), getPos().west().north());
        boolean value = false;
        for (BlockPos pos : positions) {

            IBlockState state = getWorld().getBlockState(pos);
            if (state.getBlock() == ModBlocks.PEDESTAL && state.getValue(BlockPedestal.TYPE).getMetadata() == 0) {

                TileEntity tileEntity = getWorld().getTileEntity(pos);
                if (tileEntity instanceof TileEntityPedestal) {
                    TileEntityPedestal pedestal = (TileEntityPedestal) tileEntity;
                    ItemStack stack = getStack(ConfigHandler.netherPortalPedestalItem);

                    if (stack.isEmpty() || pedestal.getStack().isEmpty() || pedestal.getStack().getItem() != stack.getItem()) {
                        return false;
                    } else if (pedestal.getStack().getItem() == stack.getItem()) {
                        value = true;
                    }
                } else return false;
            } else return false;
        }
        return value;
    }
    public boolean hasRequiredItems() {
        for (int i = 2; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public boolean hasFuel() {
        return (getFuelAmount() > 0);
    }

    public void setFuel(FluidStack fluidStack) {
        if (fluidStack == null) {
            this.tank.setFluid(null);
        } else this.tank.setFluid(fluidStack);
    }

    public int getFuelCapacity() {
        return getTank().getCapacity();
    }
    public int getFuelAmount() {
        return (getFuel() != null ? getFuel().amount : 0);
    }
    public FluidStack getFuel() {
        return this.tank.getFluid();
    }
    public FluidTank getTank() {
        return this.tank;
    }
    private static ItemStack getStack(String string) {
        int meta = 0;
        if (string.contains("|")) {
            string = string.substring(string.indexOf("|") + 1);
            meta = Integer.parseInt(CharMatcher.digit().retainFrom(string));
        }
        Item item = Item.getByNameOrId(string.contains("|") ? string.substring(0, string.indexOf("|")) : string);
        if (item != null) {
            return new ItemStack(item, 1, meta);
        } else return ItemStack.EMPTY;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(getPos().add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    /* == End == */


    /* == TileEntity == */

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        tank = new FluidTankCustom(this, getFuelCapacity()).readFromNBT(compound);
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

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank) : super.getCapability(capability, facing);
    }

    /* == End == */
}