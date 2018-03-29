package terrails.netherutils.api.world;

import net.minecraft.util.math.BlockPos;

public interface IWorldData {

    BlockPos getPointPos();
    void setPointPos(BlockPos pos);

    boolean hasSpawnPoint();

    boolean hasRead();
    void hasRead(boolean dataRead);

    BlockPos getEndSpawn();
    void setEndSpawn(BlockPos pos);
}

