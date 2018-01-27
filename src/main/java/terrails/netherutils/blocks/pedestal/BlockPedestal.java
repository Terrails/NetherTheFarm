package terrails.netherutils.blocks.pedestal;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.init.ModBlocks;
import terrails.terracore.block.BlockTileEntity;

import javax.annotation.Nullable;

public class BlockPedestal extends BlockTileEntity<TileEntityPedestal> {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 1, 0, 0.0625 * 1, 0.0625 * 15, 0.0625 * 16, 0.0625 * 15);
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public BlockPedestal(String name) {
        super(Material.ROCK, name);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setHardness(2F);
        setResistance(3F);
        setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Type.NETHER));
        GameRegistry.registerTileEntity(TileEntityPedestal.class, name);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntityPedestal te = getTileEntity(world, pos);
            if (te.getStack().isEmpty() && !player.getHeldItem(hand).isEmpty()) {
                ItemStack stack = player.getHeldItem(hand);
                ItemStack stackCopy = stack.copy();

                stackCopy.setCount(1);
                te.setStack(stackCopy);
                stack.shrink(1);
                player.openContainer.detectAndSendChanges();
            } else if (!te.getStack().isEmpty()) {
                ItemStack stack = te.getStack();
                if (player.inventory.addItemStackToInventory(stack)) {
                    te.setStack(ItemStack.EMPTY);
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityPedestal te = getTileEntity(world, pos);
        if (!te.getStack().isEmpty()) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getStack());
        }
    }

    // == Basic & Rendering == \\

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TESRPedestal());

        for (Type enumType : Type.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.PEDESTAL), new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_pedestal"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.PEDESTAL), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_pedestal"), "inventory"));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Type enumType : Type.values()) {
            items.add(new ItemStack(this, 1, enumType.getMetadata()));
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        if (meta == 1) {
            return this.getDefaultState().withProperty(TYPE, Type.END);
        } else return this.getDefaultState().withProperty(TYPE, Type.NETHER);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPedestal();
    }

    public enum Type implements IStringSerializable {
        NETHER(0, "nether"),
        END(1, "end");

        private static final Type[] META_LOOKUP = new Type[values().length];
        private int meta;
        private String name;

        Type(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMetadata() {
            return meta;
        }

        public String toString() {
            return this.name;
        }

        public static Type byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        static {
            for (Type enumType : values()) {
                META_LOOKUP[enumType.getMetadata()] = enumType;
            }
        }
    }

    // == End == \\
}
