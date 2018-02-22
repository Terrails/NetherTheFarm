package terrails.netherutils.api.portal;

import net.minecraft.util.math.BlockPos;

public interface IPortalSlave {

    void setMasterPos(BlockPos pos);
    BlockPos getMasterPos();

    void setMasterDimension(int dimension);
    int getMasterDimension();

    BlockPos getBlockPos();
    int getDimension();

    boolean isNether();
}
