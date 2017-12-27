package terrails.netherutils.blocks;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.client.render.TESRTank;
import terrails.netherutils.tileentity.TileEntityTank;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class BlockTank extends BlockBase {

    public BlockTank(String name) {
        super(Material.IRON, name);
        setDefaultState(this.blockState.getBaseState().withProperty(HAS_WATER, false));
        setHarvestLevel("pickaxe", 1);
        setHardness(5.0F);
        setTickRandomly(true);
        setResistance(5.0F);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        GameRegistry.registerTileEntity(TileEntityTank.class, name);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TESRTank());
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TileEntityTank ? ((TileEntityTank) tileEntity).getComparatorStrength() : 0;
    }
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof  TileEntityTank ? ((TileEntityTank) tileEntity).getLightLevel() : 0;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<>();
        TileEntityTank tile = getTileEntity(world, pos);

        final IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

        if (fluidHandler != null) {
            final FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(new ItemStack(this), fluidHandler, Integer.MAX_VALUE, null, true);

            if (fluidActionResult.isSuccess()) {
                drops.add(fluidActionResult.getResult());
            }
            /*
            FluidTank tank = tile.getTank();
            ItemStack stack = new ItemStack(state.getBlock());
            if (tank != null && tank.getFluid() != null) {
                stack.setTagCompound(tank.writeToNBT(new NBTTagCompound()));
              //  if (stack.getTagCompound() != null) {
                  //  stack.getTagCompound().setInteger("Capacity", tank.getCapacity());
             //   }
            }
            items.add(stack);
            */
        }
       // drops.addAll(items);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityTank tile = (TileEntityTank) world.getTileEntity(pos);

        if (tile != null) {
            FluidTank tank = tile.getTank();
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null) {
                tank.readFromNBT(compound);
            }
        }
    }
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        final TileEntityTank tileEntityTank = getTileEntity(worldIn, pos);

        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND && FluidUtil.getFluidHandler(playerIn.getHeldItem(hand)) == null) {
            getFluidDataForDisplay(tileEntityTank.getTank().getTankProperties()).forEach(playerIn::sendMessage);
        }
        return FluidUtil.interactWithFluidHandler(playerIn, hand, tileEntityTank.getTank()) || FluidUtil.getFluidHandler(playerIn.getHeldItem(hand)) != null;
    }

    public static final PropertyBool HAS_WATER = PropertyBool.create("has_water");

    @Override
    @SuppressWarnings("deprecation")
    public Material getMaterial(IBlockState state) {
        if (state.getValue(HAS_WATER)) {
            return Material.WATER;
        }
        return super.getMaterial(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HAS_WATER);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HAS_WATER) ? 1 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HAS_WATER, meta == 1);
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTank();
    }

    private static List<ITextComponent> getFluidDataForDisplay(IFluidTankProperties[] tankProperties) {
        final List<ITextComponent> data = new ArrayList<>();

        for (final IFluidTankProperties property : tankProperties) {
            final FluidStack fluidStack = property.getContents();

            boolean hasFluid = false;

            if (fluidStack != null) {
                hasFluid = true;
                data.add(new TextComponentString("[" + TextFormatting.RED + "NetherUtils" + TextFormatting.RESET + "] " + TextFormatting.GREEN + "" + fluidStack.amount + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + property.getCapacity() + TextFormatting.GRAY + " (" + TextFormatting.AQUA + fluidStack.getLocalizedName() + TextFormatting.GRAY + ")"));
            }

            if (!hasFluid) {
                data.add(new TextComponentString("[" + TextFormatting.RED + "NetherUtils" + TextFormatting.RESET + "] " + TextFormatting.GREEN + "0" + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + property.getCapacity()));
            }
        }
        return data;
    }
    private TileEntityTank getTileEntity(IBlockAccess world, BlockPos pos) {
        return (TileEntityTank) world.getTileEntity(pos);
    }
}
