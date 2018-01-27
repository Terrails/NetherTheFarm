package terrails.netherutils.entity.capabilities.portal;

import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.capabilities.IPortal;

public class Portal implements IPortal {

    private BlockPos lastEndMasterPos = BlockPos.ORIGIN;

    @Override
    public BlockPos lastMasterPos() {
        return this.lastEndMasterPos;
    }

    @Override
    public void setLastMasterPos(BlockPos pos) {
        this.lastEndMasterPos = pos;
    }
}
