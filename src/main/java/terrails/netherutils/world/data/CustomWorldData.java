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
        setEndSpawn(new BlockPos(compound.getInteger("EndPosX"), compound.getInteger("EndPosY"), compound.getInteger("EndPosZ")));
        hasRead(true);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("PosX", getPointPos().getX());
        compound.setInteger("PosY", getPointPos().getY());
        compound.setInteger("PosZ", getPointPos().getZ());
        compound.setTag("Portal", PortalRegistry.serializeNBT());
        compound.setBoolean("DataRead", hasRead);
        compound.setInteger("EndPosX", getEndSpawn().getX());
        compound.setInteger("EndPosY", getEndSpawn().getY());
        compound.setInteger("EndPosZ", getEndSpawn().getZ());
        return compound;
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
    private BlockPos endPos = BlockPos.ORIGIN;
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

    @Override
    public BlockPos getEndSpawn() {
        return this.endPos;
    }

    @Override
    public void setEndSpawn(BlockPos pos) {
        this.endPos = pos;
        markDirty();
    }
}