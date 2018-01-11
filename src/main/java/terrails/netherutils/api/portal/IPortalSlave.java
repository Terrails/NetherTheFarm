package terrails.netherutils.api.portal;

import net.minecraft.util.math.BlockPos;

public interface IPortalSlave {

    void setMasterPos(BlockPos pos);
    BlockPos getMasterPos();

    BlockPos getBlockPos();
    int getDimension();
}
