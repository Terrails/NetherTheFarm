package terrails.netherthefarm.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import terrails.terracore.block.tile.TileEntityBase;

public class TileEntityObelisk extends TileEntityBase {

    private boolean isHasTESR;

    public boolean hasTESR() {
        return isHasTESR;
    }

    public void setHasTESR(boolean hasTESR) {
        isHasTESR = hasTESR;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        setHasTESR(compound.getBoolean("hasTESR"));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasTESR", hasTESR());
        return super.writeToNBT(compound);
    }
}