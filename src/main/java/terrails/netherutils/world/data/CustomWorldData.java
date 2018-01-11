package terrails.netherutils.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.blocks.portal.PortalRegistry;

import javax.annotation.Nonnull;

public class CustomWorldData extends WorldSavedData implements IWorldData {

    private static final String NAME = "NetherUtils_WorldData";

    public CustomWorldData() {
        super(NAME);
    }

    public CustomWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        setPointPos(new BlockPos(compound.getInteger("PosX"), compound.getInteger("PosY"), compound.getInteger("PosZ")));
        if (compound.hasKey("Portal")) {
            if (PortalRegistry.LIST.size() > 0) {
                PortalRegistry.LIST.clear();
            }
            PortalRegistry.LIST.addAll(PortalRegistry.deserializeNBT((NBTTagCompound) compound.getTag("Portal")));
            Constants.Log.info("Successfully loaded portal data from world!");
        }
        hasRead(true);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("PosX", getPointPos().getX());
        compound.setInteger("PosY", getPointPos().getY());
        compound.setInteger("PosZ", getPointPos().getZ());
        compound.setTag("Portal", PortalRegistry.serializeNBT());
        compound.setBoolean("DataRead", hasRead);
        return compound;
    }

    public static class Event {

        @SubscribeEvent
        public void worldUnload(WorldEvent.Save event) {
            IWorldData data = CustomWorldData.get(event.getWorld());
            if (data != null) {
         //       PortalRegistry.LIST.clear();
        //     data.hasRead(false);
            }
        }
    }

    public static CustomWorldData get(World world) {
        CustomWorldData data = (CustomWorldData) world.loadData(CustomWorldData.class, NAME);
        if (data == null) {
            data = new CustomWorldData();
            world.setData(NAME, data);
        }
        return data;
    }


    private BlockPos pos = BlockPos.ORIGIN;
    private boolean hasRead;

    @Override
    public boolean hasRead() {
        return this.hasRead;
    }

    @Override
    public void hasRead(boolean hasRead) {
        this.hasRead = hasRead;
        markDirty();
    }

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



             /*
            MapStorage storage = world.getMapStorage();
            CustomWorldData instance = (CustomWorldData) storage.getOrLoadData(CustomWorldData.class, DATA_NAME);

            if (instance == null) {
                instance = new CustomWorldData(DATA_NAME);
                storage.setData(DATA_NAME, instance);
            }
            return instance;
            */
}