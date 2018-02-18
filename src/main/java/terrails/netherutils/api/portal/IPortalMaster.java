package terrails.netherutils.api.portal;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public interface IPortalMaster {

    void setSlavePos(BlockPos pos);
    BlockPos getSlavePos();

    void isActive(boolean isActive);
    boolean isActive();

    BlockPos getBlockPos();
    int getDimension();

    boolean isNether();
}
