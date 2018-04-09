package terrails.netherutils.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.terracore.block.BlockBase;

import java.util.Random;

public class BlockSoulSoil extends BlockBase {

    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
    private static final AxisAlignedBB FARMLAND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    public BlockSoulSoil(String name) {
        super(Material.GROUND);
        setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        setUnlocalizedName(NetherUtils.MOD_ID + "." + name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, 0));
        this.setTickRandomly(true);
        this.setLightOpacity(255);
        this.setSoundType(SoundType.SAND);
        this.setHardness(0.6F);
        this.setHarvestLevel("shovel", 0);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.motionX *= 0.4D;
        entityIn.motionZ *= 0.4D;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FARMLAND_AABB;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        EnumPlantType type = plantable.getPlantType(world, pos.up());
        return type == EnumPlantType.Crop || type == EnumPlantType.Plains || type == EnumPlantType.Nether || super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }


    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int i = state.getValue(MOISTURE);

        if (!this.hasLava(worldIn, pos) && !worldIn.isRainingAt(pos.up())) {
            if (i > 0) {
                worldIn.setBlockState(pos, state.withProperty(MOISTURE, (i - 1)), 2);
            } else if (!this.hasCrops(worldIn, pos)) {
                turnToSoulSand(worldIn, pos);
            }
        } else if (i < 7) {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, 7), 2);
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!worldIn.isRemote && entityIn.canTrample(worldIn, this, pos, fallDistance))
        {
            turnToSoulSand(worldIn, pos);
        }

        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    protected static void turnToSoulSand(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D).offset(pos);

        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb)) {
            double d0 = Math.min(axisalignedbb.maxY - axisalignedbb.minY, axisalignedbb.maxY - entity.getEntityBoundingBox().minY);
            entity.setPositionAndUpdate(entity.posX, entity.posY + d0 + 0.001D, entity.posZ);
        }
    }

    private boolean hasCrops(World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof net.minecraftforge.common.IPlantable && canSustainPlant(worldIn.getBlockState(pos), worldIn, pos, net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable) block);
    }

    private boolean hasLava(World worldIn, BlockPos pos) {
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.LAVA) {
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            turnToSoulSand(worldIn, pos);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            turnToSoulSand(worldIn, pos);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.SOUL_SAND);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(MOISTURE, (meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(MOISTURE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MOISTURE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
