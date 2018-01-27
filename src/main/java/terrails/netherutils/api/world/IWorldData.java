package terrails.netherutils.api.world;

import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.portal.IPortalMaster;

import java.util.List;

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

    boolean hasRead();
    void hasRead(boolean dataRead);

    BlockPos getEndSpawn();
    void setEndSpawn(BlockPos pos);
}

