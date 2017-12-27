package terrails.netherutils.api.world;

import net.minecraft.util.math.BlockPos;

public interface IWorldData {

    /**
     * Get the blockPos of the spawn point
     *
     * @return {@link BlockPos} of the spawn point
     */
    BlockPos getPointPos();

    /**
     * Set the blockPos of the spawn point
     *
     * @param pos {@link BlockPos} of the spawn point
     */
    void setPointPos(BlockPos pos);

    /**
     * Checks if the world has a spawn point
     *
     * @return the boolean value
     */
    boolean hasSpawnPoint();
}

