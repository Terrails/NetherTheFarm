package terrails.netherutils.entity.capabilities.obelisk;

import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.capabilities.IObelisk;

import java.util.Optional;

public class Obelisk implements IObelisk {

    private BlockPos pos;
    private int dim = Integer.MIN_VALUE;

    @Override
    public boolean hasObelisk() {
        return !(getObeliskPos().equals(BlockPos.ORIGIN));
    }

    @Override
    public BlockPos getObeliskPos() {
        return this.pos != null ? this.pos : BlockPos.ORIGIN;
    }

    @Override
    public void setObeliskPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public int getObeliskDim() {
        return this.dim;
    }

    @Override
    public void setObeliskDim(int dim) {
        this.dim = dim;
    }
}
