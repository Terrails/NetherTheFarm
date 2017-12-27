package terrails.netherutils.world.nether.trees;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import terrails.netherutils.blocks.wood.BlockNTFLeaf;
import terrails.netherutils.blocks.wood.BlockNTFLog;
import terrails.netherutils.blocks.wood.WoodType;
import terrails.netherutils.init.ModBlocks;

import java.util.Random;

public class WorldGeneratorHellwood extends WorldGenAbstractTree {

    public final IBlockState log;
    public final IBlockState leaves;
    public final IBlockState[] growBlock;

    public WorldGeneratorHellwood(boolean notify) {
        this(notify, Blocks.NETHERRACK.getDefaultState());
    }

    public WorldGeneratorHellwood(boolean notify, IBlockState... replace) {
        super(notify);
        this.log = ModBlocks.LOG.getDefaultState().withProperty(BlockNTFLog.VARIANT, WoodType.HELL);
        this.leaves = ModBlocks.LEAVES.getDefaultState().withProperty(BlockNTFLeaf.VARIANT, WoodType.HELL);
        this.growBlock = replace;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i = rand.nextInt(3) + 4;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < worldIn.getHeight()) {
                            if (!isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockPos down = position.down();
                IBlockState state = worldIn.getBlockState(down);
                boolean isSoil = false;
                for (IBlockState blockState : growBlock) {
                    if (!blockState.getPropertyKeys().isEmpty() || !state.getPropertyKeys().isEmpty()) {
                        for (IProperty property : state.getPropertyKeys()) {
                            for (IProperty property1 : blockState.getPropertyKeys())
                                if (state.getValue(property) == blockState.getValue(property1)) {
                                    isSoil = true;
                                }
                        }
                    } else if (state.getPropertyKeys().isEmpty() || blockState.getPropertyKeys().isEmpty()) {
                        isSoil = state.getBlock() == blockState.getBlock();
                    }
                }
                if (isSoil && position.getY() < worldIn.getHeight() - i - 1) {
                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        int i4 = i3 - (position.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
                                    BlockPos blockpos = new BlockPos(k1, i3, i2);
                                    state = worldIn.getBlockState(blockpos);

                                    if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int j3 = 0; j3 < i; ++j3) {
                        BlockPos upN = position.up(j3);
                        state = worldIn.getBlockState(upN);

                        if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE) {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(j3), this.log);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isReplaceable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        boolean isReplacable = false;
        for (IBlockState blockState : growBlock) {
            if (state.getPropertyKeys().isEmpty()) {
                if (!isReplacable) isReplacable = state == blockState;
            } else {
                for (IProperty property : state.getPropertyKeys()) {
                    if (state.getValue(property) == blockState) {
                        if (!isReplacable) isReplacable = true;
                    }
                }
            }
        }
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || isReplacable;
    }
}
