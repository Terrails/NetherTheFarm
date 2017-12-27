package terrails.netherutils.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import terrails.netherutils.api.world.IWorldData;

import javax.annotation.Nonnull;

public class CustomWorldData extends WorldSavedData implements IWorldData {

    private static final String DATA_NAME = "NetherUtils_SpawnData";

    public CustomWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        setPointPos(new BlockPos(compound.getInteger("PosX"), compound.getInteger("PosY"), compound.getInteger("PosZ")));
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("PosX", getPointPos().getX());
        compound.setInteger("PosY", getPointPos().getY());
        compound.setInteger("PosZ", getPointPos().getZ());
        return compound;
    }

    public static CustomWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        assert storage != null;
        CustomWorldData instance = (CustomWorldData) storage.getOrLoadData(CustomWorldData.class, DATA_NAME);

        if (instance == null) {
            instance = new CustomWorldData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    private BlockPos pos = BlockPos.ORIGIN;

    @Override
    public BlockPos getPointPos() {
        return this.pos;
    }

    @Override
    public void setPointPos(BlockPos pos) {
        this.pos = pos;
        markDirty();
    }

    @Override
    public boolean hasSpawnPoint() {
        return !(getPointPos().equals(BlockPos.ORIGIN));
    }
}