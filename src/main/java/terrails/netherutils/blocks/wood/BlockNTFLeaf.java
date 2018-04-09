package terrails.netherutils.blocks.wood;

import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModItems;
import terrails.terracore.registry.IItemBlock;
import terrails.terracore.registry.client.ICustomModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockNTFLeaf extends BlockLeaves implements IItemBlock, ICustomModel {

    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.create("variant", WoodType.class, predicate -> {
        assert predicate != null;
        return predicate.getMetadata() < 4;
    });

    public BlockNTFLeaf(String name) {
        this.setRegistryName(name);
        this.setCreativeTab(NetherUtils.TAB_NETHER_UTILS);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.HELL).withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
    }

    @Override
    public void initModel() {
        for (WoodType enumType : WoodType.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.LEAVES), new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_leaves"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.LEAVES), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_leaves"), "inventory"));
        }
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockLeaf(this);
    }

    private static int tick;
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(VARIANT) == WoodType.SOUL) {
            if (tick == 0) {
                super.randomDisplayTick(stateIn, worldIn, pos, rand);
                double d0 = (double) ((float) pos.getX() + rand.nextFloat());
                double d1 = (double) pos.getY() - 0.05D;
                double d2 = (double) ((float) pos.getZ() + rand.nextFloat());

                // Using Soul Sand as color, similar int to it is: 3 or 5
                worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(Blocks.SOUL_SAND.getDefaultState()));
            }
            tick+=1;
            if (tick > 5)
                tick = 0;
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (WoodType enumType : WoodType.values()) {
            items.add(new ItemStack(this, 1, enumType.getMetadata()));
        }
    }

    @Override
    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
        int chance2 = chance + 40;
        if (state.getValue(VARIANT) == WoodType.HELL && worldIn.rand.nextInt(chance) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.GHAST_TEAR));
        } else if (state.getValue(VARIANT) == WoodType.ASH && worldIn.rand.nextInt(chance) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.GLOWSTONE_DUST));
        } else if (state.getValue(VARIANT) == WoodType.SOUL && worldIn.rand.nextInt(chance2) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(ModItems.FORBIDDEN_FRUIT));
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.SAPLING.getDefaultState().withProperty(VARIANT, state.getValue(VARIANT)).getBlock());
    }

    @Override
    protected int getSaplingDropChance(IBlockState state) {
        return state.getValue(VARIANT) == WoodType.HELL ? 40 : super.getSaplingDropChance(state);
    }

    @Override
    protected @Nonnull ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, this.getType(meta)).withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {

        int i = 0;
        i = i | (state.getValue(VARIANT)).getMetadata();

        if (!(state.getValue(DECAYABLE)))
        {
            i |= 4;
        }

        if ((state.getValue(CHECK_DECAY)))
        {
            i |= 8;
        }

        return i;
    }

    @Override
    protected @Nonnull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
            player.addStat(StatList.getBlockStats(this));
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    public WoodType getType(int meta) {
        return WoodType.byMetadata((meta & 3) % 4);
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }

    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this, 1, world.getBlockState(pos).getValue(VARIANT).getMetadata()));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return Blocks.LEAVES.isOpaqueCube(state);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return Blocks.LEAVES.getBlockLayer();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return Blocks.LEAVES.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}
