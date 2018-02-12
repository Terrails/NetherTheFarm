package terrails.netherutils.api.capabilities;

import net.minecraft.util.math.BlockPos;

public interface IObelisk {

    boolean hasObelisk();

    BlockPos getObeliskPos();
    void setObeliskPos(BlockPos pos);

    int getObeliskDim();
    void setObeliskDim(int dim);
}