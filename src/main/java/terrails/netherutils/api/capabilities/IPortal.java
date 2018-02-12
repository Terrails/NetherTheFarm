package terrails.netherutils.api.capabilities;

import net.minecraft.util.math.BlockPos;

public interface IPortal {

    BlockPos lastMasterPos();
    void setLastMasterPos(BlockPos pos);
}
