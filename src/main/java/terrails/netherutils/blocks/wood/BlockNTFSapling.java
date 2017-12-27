package terrails.netherutils.blocks.wood;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.Loader;
import terrails.netherutils.Constants;
import terrails.netherutils.world.nether.TreesGenerator;
import terrails.netherutils.world.nether.trees.WorldGeneratorAshwood;
import terrails.netherutils.world.nether.trees.WorldGeneratorHellwood;
import terrails.netherutils.world.nether.trees.WorldGeneratorSoulwood;

import java.util.Random;

public class BlockNTFSapling extends BlockBush implements IGrowable {

    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.create("variant", WoodType.class);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);


    public BlockNTFSapling(String name) {
        setRegistryName(name);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.HELL).withProperty(STAGE, Integer.valueOf(0)));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        boolean condition = false;
        if (Loader.isModLoaded("biomesoplenty")) {
            for (IBlockState blockState : TreesGenerator.blockStates) {
                if (!condition) condition = blockState == soil && worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
            }
        } else condition = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && soil.getBlock() == Blocks.NETHERRACK;
        return condition;
    }
    @Override
    protected boolean canSustainBush(IBlockState state) {
        boolean condition = false;
        if (Loader.isModLoaded("biomesoplenty")) {
            for (IBlockState blockState : TreesGenerator.blockStates) {
                System.out.println(blockState);
                if (!condition) condition = blockState == state;
            }
        } else condition = state.getBlock() == Blocks.NETHERRACK;
        return condition;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);

            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }
    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE).intValue() == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }
    public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return;
        WorldGenerator worldgenerator = null;
        int i = 0;
        int j = 0;

        if (state.getValue(VARIANT) == WoodType.HELL)
            worldgenerator = new WorldGeneratorHellwood(true, TreesGenerator.blockStates);
        else if (state.getValue(VARIANT) == WoodType.ASH)
            worldgenerator = new WorldGeneratorAshwood(true, TreesGenerator.blockStates);
        else if (state.getValue(VARIANT) == WoodType.SOUL)
            worldgenerator = new WorldGeneratorSoulwood(true, TreesGenerator.blockStates);

        IBlockState iblockstate2 = Blocks.AIR.getDefaultState();
        worldIn.setBlockState(pos, iblockstate2, 4);

        if (worldgenerator != null && !worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
            worldIn.setBlockState(pos, state, 4);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (WoodType enumType : WoodType.values()) {
            items.add(new ItemStack(this, 1, enumType.getMetadata()));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return (double)worldIn.rand.nextFloat() < 0.45D;
    }
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        this.grow(worldIn, pos, state, rand);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, WoodType.byMetadata(meta & 7)).withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();
        i = i | state.getValue(STAGE).intValue() << 3;
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {VARIANT, STAGE});
    }
}
