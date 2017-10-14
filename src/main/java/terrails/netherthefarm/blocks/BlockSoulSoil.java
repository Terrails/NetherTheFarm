package terrails.netherthefarm.blocks;

import biomesoplenty.common.util.entity.PlayerUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.init.ModBlocks;
import terrails.terracore.block.BlockBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber
public class BlockSoulSoil extends BlockBase {

    public static final PropertyBool MOISTURE = PropertyBool.create("moisture");
    protected static final AxisAlignedBB FARMLAND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    public BlockSoulSoil(String name) {
        super(Material.GROUND, name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, false));
        this.setTickRandomly(true);
        this.setLightOpacity(255);
        this.setHardness(0.6F);
        this.setHarvestLevel("shovel", 0);
    }

    @SubscribeEvent
    public static void fromSoulSandToFarmLand(UseHoeEvent event) {
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (event.getWorld().isAirBlock(event.getPos().up())) {
            if (block == Blocks.SOUL_SAND) {
                boolean isWoodenHoe = event.getCurrent().getItem().getUnlocalizedName().toLowerCase().contains("wood");
                if (!isWoodenHoe) {
                    setBlock(event.getCurrent(), event.getEntityPlayer(), event.getWorld(), event.getPos(), ModBlocks.SOUL_SOIL.getDefaultState().withProperty(MOISTURE, false));
                } else if (!event.getWorld().isRemote) {
                    Constants.playerMessage(event.getEntityPlayer(), "You cannot use a wooden hoe on soul sand!");
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        boolean stateValue = state.getValue(MOISTURE);
        if (!hasLava(worldIn, pos) || worldIn.isRainingAt(pos.up())) {
            if (stateValue) {
                worldIn.setBlockState(pos, state.withProperty(MOISTURE, false), 2);
            } else if (!this.hasCrops(worldIn, pos)) {
                turnToSoulSand(worldIn, pos);
            }
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        } else if (!stateValue) {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, true), 2);
        }
    }
    private static void setBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state) {
        world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        player.swingArm(PlayerUtil.getHandForItemAndMeta(player, player.getActiveItemStack().getItem(), player.getActiveItemStack().getMetadata()));

        if (!world.isRemote) {
            world.setBlockState(pos, state, 11);
            stack.damageItem(1, player);
            AxisAlignedBB axisalignedbb = ModBlocks.SOUL_SOIL.getDefaultState().getCollisionBoundingBox(world, pos).offset(pos);
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb)) {
                entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
            }
        }
    }
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.motionX *= 0.4D;
        entityIn.motionZ *= 0.4D;
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.SOUL_SAND);
    }
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            turnToSoulSand(worldIn, pos);
        }
    }
    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!worldIn.isRemote && entityIn.canTrample(worldIn, this, pos, fallDistance)) {
            turnToSoulSand(worldIn, pos);
        }
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            turnToSoulSand(worldIn, pos);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FARMLAND_AABB;
    }
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
    }
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MOISTURE);
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess blockAccess, IBlockState blockState, BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    private boolean hasLava(World worldIn, BlockPos pos) {
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.LAVA) {
                return true;
            }
        }
        return false;
    }
    private void turnToSoulSand(World worldIn, BlockPos pos) {
        IBlockState iblockstate = Blocks.SOUL_SAND.getDefaultState();
        worldIn.setBlockState(pos, iblockstate);
        if (iblockstate.getCollisionBoundingBox(worldIn, pos) != null) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(worldIn, pos).offset(pos);

            for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb)) {
                entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
            }
        }
    }
    private boolean hasCrops(World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof IPlantable && canSustainPlant(worldIn.getBlockState(pos), worldIn, pos, EnumFacing.UP, (IPlantable) block);
    }
}