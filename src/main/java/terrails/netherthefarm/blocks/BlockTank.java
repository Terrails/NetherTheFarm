package terrails.netherthefarm.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.NetherTheFarm;
import terrails.netherthefarm.render.TileEntityTankRenderer;
import terrails.netherthefarm.tile.TileEntityTank;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockTank extends BlockBase implements ITileEntityProvider {

    public BlockTank(String name) {
        super(Material.IRON, name, true);
        setHarvestLevel("pickaxe", 1);
        setHardness(5.0F);
        setResistance(5.0F);
        setCreativeTab(Constants.NTF_TAB);
        GameRegistry.registerTileEntity(TileEntityTank.class, name);
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof  TileEntityTank ? ((TileEntityTank) tileEntity).comparatorStrength() : 0;
    }
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof  TileEntityTank ? ((TileEntityTank) tileEntity).getLightLevel() : 0;
    }

    public enum NBTData {
        AMOUNT("amount"),
        CAPACITY("capacity"),
        REGISTRY_NAME("regName"),
        LOCALIZED_NAME("localName");

        private final String name;

        NBTData(String key) {
            this.name = key;
        }

        public String getName() {
            return name;
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
    }
    private static NBTTagCompound getTags(FluidTank fluidTank, NBTTagCompound nbtTagCompound) {
        NBTTagCompound compound = nbtTagCompound == null ? new NBTTagCompound() : nbtTagCompound;
        if (fluidTank != null && fluidTank.getFluid() != null) {
            compound.setInteger(NBTData.AMOUNT.getName(), fluidTank.getFluidAmount());
            compound.setInteger(NBTData.CAPACITY.getName(), fluidTank.getCapacity());
            compound.setString(NBTData.REGISTRY_NAME.getName(), fluidTank.getFluid().getFluid().getName());
            compound.setString(NBTData.LOCALIZED_NAME.getName(), new FluidStack(FluidRegistry.getFluid(compound.getString(NBTData.REGISTRY_NAME.getName())), compound.getInteger(NBTData.AMOUNT.getName())).getLocalizedName());
        }
        return compound;
    }
    protected TileEntityTank getTileEntity(IBlockAccess world, BlockPos pos) {
        return (TileEntityTank) world.getTileEntity(pos);
    }
    public static List<ITextComponent> getFluidDataForDisplay(IFluidTankProperties[] tankProperties) {
        final List<ITextComponent> data = new ArrayList<>();

        for (final IFluidTankProperties property : tankProperties) {
            final FluidStack fluidStack = property.getContents();

            boolean hasFluid = false;

            if (fluidStack != null) {
                hasFluid = true;
                data.add(new TextComponentString("[" + TextFormatting.RED + "NetherTheFarm" + TextFormatting.RESET + "] " + TextFormatting.GREEN + "" + fluidStack.amount + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + property.getCapacity() + TextFormatting.GRAY + " (" + TextFormatting.AQUA + fluidStack.getLocalizedName() + TextFormatting.GRAY + ")"));
                //  data.add(new TextComponentTranslation("tile.netherthefarm:tank.fluid.desc", fluidStack.amount, property.getCapacity(), fluidStack.getLocalizedName()));
            }

            if (!hasFluid) {
                data.add(new TextComponentString("[" + TextFormatting.RED + "NetherTheFarm" + TextFormatting.RESET + "] " + TextFormatting.GREEN + "0" + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + property.getCapacity()));
                //    data.add(new TextComponentTranslation("tile.netherthefarm:tank.fluid.empty", property.getCapacity()));
            }
        }
        return data;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<>();
        TileEntityTank tile = getTileEntity(world, pos);

        if (tile != null) {
            FluidTank tank = tile.getTank();
            ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock());
            if (tank != null && tank.getFluid() != null)
                stack.setTagCompound(getTags(tank, null));
            items.add(stack);
        }
        return items;
    }
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityTank tile = (TileEntityTank) world.getTileEntity(pos);

        if (tile != null) {
            FluidTank tank = tile.getTank();
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound compound = getTags(tank, tagCompound);
            if (getTags(tank, compound).getInteger(NBTData.AMOUNT.getName()) != 0)
                tank.setFluid(new FluidStack(FluidRegistry.getFluid(compound.getString(NBTData.REGISTRY_NAME.getName())), compound.getInteger(NBTData.AMOUNT.getName())));
        }
    }
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        final TileEntityTank tileEntityTank = getTileEntity(worldIn, pos);

        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND && FluidUtil.getFluidHandler(playerIn.getHeldItem(hand)) == null) {
            getFluidDataForDisplay(tileEntityTank.getTank().getTankProperties()).forEach(playerIn::sendMessage);
        }
        if (FluidUtil.interactWithFluidHandler(playerIn, hand, tileEntityTank.getTank()))
            return true;
        return FluidUtil.getFluidHandler(playerIn.getHeldItem(hand)) != null;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTank();
    }
}
