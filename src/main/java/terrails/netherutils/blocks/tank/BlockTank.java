package terrails.netherutils.blocks.tank;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.terracore.block.BlockTileEntity;
import terrails.netherutils.config.ConfigHandler;
import terrails.terracore.registry.IItemBlock;
import terrails.terracore.registry.client.ICustomModel;

import java.util.Objects;

public class BlockTank extends BlockTileEntity<TileEntityTank> implements IItemBlock, ICustomModel {

    public BlockTank(String name) {
        super(Material.IRON);
        setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        setUnlocalizedName(NetherUtils.MOD_ID + "." + name);
        setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 8));
        setHarvestLevel("pickaxe", 1);
        setHardness(5.0F);
        setTickRandomly(true);
        setResistance(5.0F);
        setCreativeTab(NetherUtils.TAB_NETHER_UTILS);
        GameRegistry.registerTileEntity(TileEntityTank.class, name);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockTank(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TESRTank());
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this);

        if (ConfigHandler.tankKeepContent) {
            IFluidHandler fluidHandler = getFluidHandler(world, pos);
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);
            if (fluidHandler != null && fluidHandlerItem != null) {
                fluidHandlerItem.fill(fluidHandler.drain(Integer.MAX_VALUE, false), true);
            }
        }
        drops.add(stack);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(this);
        if (ConfigHandler.tankKeepContent) {
            IFluidHandler fluidHandler = getFluidHandler(world, pos);
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);

            if (fluidHandler != null && fluidHandlerItem != null) {
                fluidHandlerItem.fill(fluidHandler.drain(Integer.MAX_VALUE, false), true);
            }
        }
        return stack;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote || !ConfigHandler.tankKeepContent) {
            return;
        }

        IFluidHandler fluidHandler = getFluidHandler(world, pos);
        IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);

        if (fluidHandler != null && fluidHandlerItem != null) {
            fluidHandler.fill(fluidHandlerItem.drain(Integer.MAX_VALUE, false), true);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        IFluidHandler handler = getFluidHandler(worldIn, pos);

        if (handler != null) {

            if (!heldItem.isEmpty() && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IItemHandler playerInv = new InvWrapper(player.inventory);

                FluidActionResult fillResult = FluidUtil.tryFillContainerAndStow(heldItem, handler, playerInv, Integer.MAX_VALUE, player);
                if (fillResult.isSuccess()) {
                    player.setHeldItem(hand, fillResult.getResult());
                    return true;
                }

                FluidActionResult drainResult = FluidUtil.tryEmptyContainerAndStow(heldItem, handler, playerInv, Integer.MAX_VALUE, player);
                if (drainResult.isSuccess()) {
                    player.setHeldItem(hand, drainResult.getResult());
                    return true;
                }
            }

       //     FluidUtil.interactWithFluidHandler(player, hand, worldIn, pos, facing);

            if (!worldIn.isRemote && player.isSneaking() && hand == EnumHand.MAIN_HAND) {
                for (final IFluidTankProperties property : getTileEntity(worldIn, pos).getTank().getTankProperties()) {
                    FluidStack fluidStack = property.getContents();

                    String GREEN = TextFormatting.GREEN + "";
                    String GOLD = TextFormatting.GOLD + "";

                    if (fluidStack == null) {
                        Constants.Log.playerMessage(player, GREEN + "0" + GOLD + "/" + GREEN + property.getCapacity());
                    } else {
                        String GRAY = TextFormatting.GRAY + "";
                        String AQUA = TextFormatting.AQUA + "";
                        Constants.Log.playerMessage(player, GREEN + fluidStack.amount + GOLD + "/" + GREEN + property.getCapacity() + GRAY + " (" + AQUA + fluidStack.getLocalizedName() + GRAY + ")");
                    }
                }
            }

            return player.getHeldItem(hand).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        }

        return false;
    }


    // == Basic & Rendering == \\

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        return getTileEntity(world, pos) == null ? 0 : getTileEntity(world, pos).getComparatorStrength();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTank();
    }

    private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
        return getTileEntity(world, pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    // == End == \\

    // == Hydration == \\

    /* Current Issues */
    // When placing down water source next to the block it makes the water flow after the source is removed
    // Turns lava into cobblestone and obsidian, also turns itself into stone if lava is on it
    // When using PropertyBool placing down a water block next to it makes it crash because it's searching for a IBlockState with PropertyInteger named level and with values from 0 to 15
    // the fix is https://github.com/MinecraftForge/MinecraftForge/pull/4619, but needs to be merged

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    @Override
    @SuppressWarnings("deprecation")
    public Material getMaterial(IBlockState state) {
        if (state.getValue(LEVEL) > 0) {
            return Material.WATER;
        }
        return super.getMaterial(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL) == 0 ? 1 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta == 1 ? 0 : 1);
    }

    // == End == \\
}
