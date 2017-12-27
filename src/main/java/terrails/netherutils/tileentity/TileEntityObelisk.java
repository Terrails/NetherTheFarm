package terrails.netherutils.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import terrails.terracore.block.tile.TileEntityBase;

public class TileEntityObelisk extends TileEntityBase {

    private boolean hasTESR;

    public boolean hasTESR() {
        return hasTESR;
    }
    public void hasTESR(boolean hasTESR) {
        this.hasTESR = hasTESR;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        hasTESR(compound.getBoolean("hasTESR"));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasTESR", hasTESR());
        return super.writeToNBT(compound);
    }
}
