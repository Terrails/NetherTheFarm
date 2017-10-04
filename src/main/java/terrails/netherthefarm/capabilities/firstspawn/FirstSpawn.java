package terrails.netherthefarm.capabilities.firstspawn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;

import javax.annotation.Nullable;

public class FirstSpawn implements IFirstSpawn {

    private int startingFeature;
    private int hasObelisk;
    private int pointX;
    private int pointY;
    private int pointZ;
    private int obeliskDim = -1;
    private int oldPlayerDim;
 //   private NBTTagCompound nbtBase;

/*
    @Override
    public NBTTagCompound nbtTag() {
        return nbtBase;
    }

    @Override
    public void setNbtTag(NBTTagCompound nbt) {
        nbtBase = nbt;
    }

    @Override
    public boolean hasObeliskInDim(int inDim) {
        if (nbtTag().hasKey("hasObeliskDim"+inDim) && nbtTag().getBoolean("hasObeliskDim"+inDim))
            return true;
        return false;
    }
    @Override
    public void setHasObeliskDim(boolean hasObeliskDim, int inDim) {
        nbtTag().setBoolean("hasObeliskDim" + inDim, hasObeliskDim);
    }

    @Override
    public int posDimX(int inDim) {
        if (nbtTag().hasKey("obeliskPosXDim" + inDim))
            return nbtTag().getInteger("obeliskPosXDim" + inDim);
        return 0;
    }

    @Override
    public void setPosDimX(int posX, int inDim) {
        nbtTag().setInteger("obeliskPosXDim" + inDim, posX);
    }

    @Override
    public int posDimY(int inDim) {
        if (nbtTag().hasKey("obeliskPosYDim" + inDim))
            return nbtTag().getInteger("obeliskPosYDim" + inDim);
        return 0;
    }

    @Override
    public void setPosDimY(int posY, int inDim) {
        nbtTag().setInteger("obeliskPosYDim" + inDim, posY);
    }

    @Override
    public int posDimZ(int inDim) {
        if (nbtTag().hasKey("obeliskPosZDim" + inDim))
            return nbtTag().getInteger("obeliskPosZDim" + inDim);
        return 0;
    }

    @Override
    public void setPosDimZ(int posZ, int inDim) {
        nbtTag().setInteger("obeliskPosZDim" + inDim, posZ);
    }
*/
    public FirstSpawn() {
    }

    @Override
    public int hasStartingFeatures() {
        return startingFeature;
    }
    @Override
    public void hasStartingFeatures(int hasItems) {
        startingFeature = hasItems;
    }

    @Override
    public int hasObelisk() {
        return hasObelisk;
    }
    @Override
    public void hasObelisk(int hasSpawn) {
        hasObelisk = hasSpawn;
    }

    @Override
    public int posX() {
        return pointX;
    }
    @Override
    public void setPosX(int posX) {
        pointX = posX;
    }

    @Override
    public int posY() {
        return pointY;
    }
    @Override
    public void setPosY(int posY) {
        pointY = posY;
    }

    @Override
    public int posZ() {
        return pointZ;
    }
    @Override
    public void setPosZ(int posZ) {
        pointZ = posZ;
    }

    @Override
    public int obeliskDim() {
        return obeliskDim;
    }
    @Override
    public void setObeliskDim(int dim) {
        obeliskDim = dim;
    }

    @Override
    public int oldPlayerDimension() {
        return oldPlayerDim;
    }
    @Override
    public void oldPlayerDimension(int dim) {
        oldPlayerDim = dim;
    }
}
