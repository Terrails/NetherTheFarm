package terrails.netherutils.api.capabilities;

import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.portal.IPortalMaster;

public interface IPortal {

    BlockPos lastMasterPos();
    void setLastMasterPos(BlockPos pos);

}
