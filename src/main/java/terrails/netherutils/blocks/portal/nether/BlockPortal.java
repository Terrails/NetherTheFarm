package terrails.netherutils.blocks.portal.nether;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.terracore.block.BlockTileEntity;
import terrails.netherutils.blocks.portal.nether.render.TESRPortal;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.blocks.portal.PortalRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPortal extends BlockTileEntity<TileEntityPortalMaster> {

    public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 0.0625 * 16, 0.0625 * 8, 0.0625 * 16);

    public BlockPortal(String name) {
        super(Material.ROCK, name);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setHardness(4.0F);
        setResistance(12.0F);
        setHarvestLevel("pickaxe", 2);
        GameRegistry.registerTileEntity(TileEntityPortalMaster.class, name);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) {
            return;
        }

        String dimensionName = world.provider.getDimensionType().getName();

        if ((!dimensionName.equalsIgnoreCase(DimensionType.NETHER.getName()) && !dimensionName.equalsIgnoreCase(DimensionType.OVERWORLD.getName())) && placer instanceof EntityPlayer) {
            Constants.Log.playerMessage((EntityPlayer) placer, "Portal only works in the nether and overworld!");
        }

        TileEntityPortalMaster te = getTileEntity(world, pos);
        PortalRegistry.addPortal(te, world);


        if (ConfigHandler.netherPortalKeepFluid) {
            IFluidHandler fluidHandler = getFluidHandler(world, pos);
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);

            if (fluidHandler != null && fluidHandlerItem != null) {
                if (fluidHandlerItem.getTankProperties()[0].getContents() != null)
                    fluidHandler.fill(fluidHandlerItem.drain(Integer.MAX_VALUE, false), true);
            }
        }

        if (stack.getTagCompound() == null)
            return;

        NBTTagCompound compound = stack.getTagCompound();

        if (compound.getInteger("ySlave") != 0)
            te.setSlavePos(new BlockPos(compound.getInteger("xSlave"), compound.getInteger("ySlave"), compound.getInteger("zSlave")));

        if (!ConfigHandler.netherPortalKeepInventory || !compound.hasKey("Inventory"))
            return;

        try {
            te.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        } catch (NullPointerException e) {/*Empty*/}
    }

    @Override
    public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this);
        TileEntityPortalMaster te = getTileEntity(world, pos);

        if (ConfigHandler.netherPortalKeepFluid) {
            IFluidHandler fluidHandler = getFluidHandler(world, pos);
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);
            if (fluidHandler != null && fluidHandlerItem != null) {
                fluidHandlerItem.fill(fluidHandler.drain(Integer.MAX_VALUE, false), true);
            }
        }

        if (!te.isInvEmpty() || !te.getSlavePos().equals(BlockPos.ORIGIN)) {
            if (stack.getTagCompound() == null || stack.isEmpty()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound compound = stack.getTagCompound();

            if (!te.getSlavePos().equals(BlockPos.ORIGIN)) {
                compound.setInteger("xSlave", te.getSlavePos().getX());
                compound.setInteger("ySlave", te.getSlavePos().getY());
                compound.setInteger("zSlave", te.getSlavePos().getZ());
            }
            if (ConfigHandler.netherPortalKeepInventory && !te.isInvEmpty()) {
                compound.setTag("Inventory", te.inventory.serializeNBT());
            }

        }
        drops.add(stack);
    }

    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(this);
        TileEntityPortalMaster te = getTileEntity(world, pos);

        if (ConfigHandler.netherPortalKeepFluid) {
            IFluidHandler fluidHandler = getFluidHandler(world, pos);
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);

            if (fluidHandler != null && fluidHandlerItem != null) {
                fluidHandlerItem.fill(fluidHandler.drain(Integer.MAX_VALUE, false), true);
            }
        }

        if (!te.isInvEmpty() || !te.getSlavePos().equals(BlockPos.ORIGIN)) {
            if (stack.getTagCompound() == null || stack.isEmpty()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound compound = stack.getTagCompound();

            if (!te.getSlavePos().equals(BlockPos.ORIGIN)) {
                compound.setInteger("xSlave", te.getSlavePos().getX());
                compound.setInteger("ySlave", te.getSlavePos().getY());
                compound.setInteger("zSlave", te.getSlavePos().getZ());
            }
            if (ConfigHandler.netherPortalKeepInventory && !te.isInvEmpty()) {
                compound.setTag("Inventory", te.inventory.serializeNBT());
            }
        }
        return stack;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            player.openGui(NetherUtils.INSTANCE, 1, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntityPortalMaster tile = getTileEntity(world, pos);
        PortalRegistry.removePortal(tile, world);

        if (!ConfigHandler.netherPortalKeepInventory) {
            for (int i = 0; i < tile.inventory.getSlots(); i++) {
                if (!tile.inventory.getStackInSlot(i).isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tile.inventory.getStackInSlot(i));
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    // == Basic & Rendering == \\

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        PortalRegistry.removePortal(getTileEntity(world, pos), world);
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalMaster.class, new TESRPortal());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPortalMaster();
    }

    private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
        return getTileEntity(world, pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    // == End == \\
}
