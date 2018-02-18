package terrails.netherutils.api.capabilities;

import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface IObelisk {

    boolean hasObelisk();

    BlockPos getObeliskPos();
    void setObeliskPos(BlockPos pos);

    int getObeliskDim();
    void setObeliskDim(int dim);
}