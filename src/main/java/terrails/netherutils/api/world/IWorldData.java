package terrails.netherutils.api.world;

import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.portal.IPortalMaster;

import java.util.List;

public interface IWorldData {

    BlockPos getPointPos();
    void setPointPos(BlockPos pos);

    boolean hasSpawnPoint();

    boolean hasRead();
    void hasRead(boolean dataRead);

    BlockPos getEndSpawn();
    void setEndSpawn(BlockPos pos);
}

