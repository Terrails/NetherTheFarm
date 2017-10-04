package terrails.netherthefarm.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import terrails.netherthefarm.api.world.data.IWorldData;

public class CustomWorldData extends WorldSavedData implements IWorldData {

    private static final String DATA_NAME = "NetherTheFarm_SpawnPointData";

    public CustomWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        setPosX(nbt.getInteger("spawnPointPosX"));
        setPosY(nbt.getInteger("spawnPointPosY"));
        setPosZ(nbt.getInteger("spawnPointPosZ"));
        spawnPointDim(nbt.getInteger("spawnPointDimension"));
        hasSpawnPoint(nbt.getBoolean("hasSpawnPoint"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("spawnPointPosX", posX());
        compound.setInteger("spawnPointPosY", posY());
        compound.setInteger("spawnPointPosZ", posZ());
        compound.setInteger("spawnPointDimension", spawnPointDim());
        compound.setBoolean("hasSpawnPoint", hasSpawnPoint());
        return compound;
    }

    public static CustomWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        CustomWorldData instance = (CustomWorldData) storage.getOrLoadData(CustomWorldData.class, DATA_NAME);

        if (instance == null) {
            instance = new CustomWorldData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    private int pointX;
    private int pointY;
    private int pointZ;
    private int spawnPointDim = -1;
    private boolean hasSpawnPoint;

    @Override
    public int posX() {
        return pointX;
    }

    @Override
    public void setPosX(int posX) {
        pointX = posX;
        markDirty();
    }

    @Override
    public int posY() {
        return pointY;
    }

    @Override
    public void setPosY(int posY) {
        pointY = posY;
        markDirty();
    }

    @Override
    public int posZ() {
        return pointZ;
    }

    @Override
    public void setPosZ(int posZ) {
        pointZ = posZ;
        markDirty();
    }

    @Override
    public int spawnPointDim() {
        return spawnPointDim;
    }

    @Override
    public void spawnPointDim(int pointDim) {
        spawnPointDim = pointDim;
        markDirty();
    }

    @Override
    public boolean hasSpawnPoint() {
        return hasSpawnPoint;
    }

    @Override
    public void hasSpawnPoint(boolean hasSpawn) {
        hasSpawnPoint = hasSpawn;
        markDirty();
    }
}
