package terrails.netherutils.blocks.portal;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.StringUtils;
import terrails.netherutils.Constants;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.world.data.CustomWorldData;

import java.util.*;

public class PortalRegistry {

   public static List<IPortalMaster> LIST = Lists.newArrayList();

    /**
     * Adds a portal to the list and marks the world world dirty
     *
     * @param portal the portal which is being added
     * @param world the world used to mark dirty
     * @return successfully added
     * @throws NullPointerException if the world is null
     */
    public static boolean addPortal(IPortalMaster portal, World world) {
        if (world == null) throw new NullPointerException("World is null, REPORT TO NETHERUTILS ISSUE TRACKER!");

        // Check if the current portal exists in the list, if doesn't add to the list and mark dirty
        for (IPortalMaster master : LIST) {
            if (master.getBlockPos().equals(portal.getBlockPos()) && master.getDimension() == portal.getDimension()) {
                return false;
            }
        }
        LIST.add(portal);
        CustomWorldData.get(world).markDirty();
        return true;
    }

    /**
     * Removes a portal from the list if the pos and dimension are the same
     *
     * @param portal the portal which is being deleted
     * @param world the world used to mark dirty
     * @return successfully removed
     * @throws NullPointerException if the world is null
     */
    public static boolean removePortal(IPortalMaster portal, World world) {
        if (world == null) throw new NullPointerException("World is null, REPORT TO NETHERUTILS ISSUE TRACKER!");

        boolean value = false;
        Iterator<IPortalMaster> iterator = LIST.iterator();
        while (iterator.hasNext()) {
            IPortalMaster master = iterator.next();

            if (master.getDimension() == portal.getDimension()) {
                if (master.getBlockPos().equals(portal.getBlockPos()) && master.getDimension() == portal.getDimension()) {
                    iterator.remove();
                    value = true;
                }
            }
        }
        CustomWorldData.get(world).markDirty();
        return value;
    }

    /**
     * Serializes portal list for storing onto disk
     *
     * @return {@link List<IPortalMaster>} converted into {@link NBTTagCompound}
     */
    public static NBTTagCompound serializeNBT() {
        NBTTagList tagList = new NBTTagList();
        int index = 0;
        for (IPortalMaster master : LIST) {
            index++;
            NBTTagCompound nbtTag = new NBTTagCompound();
            nbtTag.setInteger("xSlave", master.getSlavePos().getX());
            nbtTag.setInteger("ySlave", master.getSlavePos().getY());
            nbtTag.setInteger("zSlave", master.getSlavePos().getZ());
            nbtTag.setBoolean("isActive", master.isActive());
            nbtTag.setInteger("xPos", master.getBlockPos().getX());
            nbtTag.setInteger("yPos", master.getBlockPos().getY());
            nbtTag.setInteger("zPos", master.getBlockPos().getZ());
            nbtTag.setInteger("Dimension", master.getDimension());
            nbtTag.setBoolean("isNether", master.isNether());
            boolean shouldAdd = true;
            int index1 = 0;
            for (IPortalMaster master1 : LIST) {
                index1++;
                if (index != index1 && master.getBlockPos().equals(master1.getBlockPos()) && master.getDimension() == master1.getDimension())
                    shouldAdd = false;
            }
            if (shouldAdd)
                tagList.appendTag(nbtTag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Portals", tagList);
        return nbt;
    }

    /**
     * Deserializes {@link NBTTagCompound} into {@link List<IPortalMaster>}
     *
     * @param nbt the NBT to deserialize
     * @return {@link List<IPortalMaster>}
     */
    public static List<IPortalMaster> deserializeNBT(NBTTagCompound nbt) {
        List<IPortalMaster> list = Lists.newArrayList();
        NBTTagList tagList = nbt.getTagList("Portals", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbtTag = tagList.getCompoundTagAt(i);
            boolean isActive = nbtTag.getBoolean("isActive");
            int dimension = nbtTag.getInteger("Dimension");
            boolean isNether = nbtTag.getBoolean("isNether");

            BlockPos slavePos = BlockPos.ORIGIN;
            if (nbtTag.getInteger("ySlave") != 0) {
                slavePos = new BlockPos(nbtTag.getInteger("xSlave"), nbtTag.getInteger("ySlave"), nbtTag.getInteger("zSlave"));
            }
            
            BlockPos blockPos = BlockPos.ORIGIN;
            if (nbtTag.getInteger("yPos") != 0) {
                blockPos = new BlockPos(nbtTag.getInteger("xPos"), nbtTag.getInteger("yPos"), nbtTag.getInteger("zPos"));
            }

            if (blockPos != BlockPos.ORIGIN)
                list.add(new PortalMaster(slavePos, blockPos, isActive, dimension, isNether));
        }
        return list;
    }

    /**
     * Goes through the config string and returns the required item for each slot
     *
     * @param slot the slot for which to get the required item
     * @param stack the stack used for the {@link IFluidHandler}
     * @return an {@link ItemStack} the slot requires, if null returns EMPTY
     */
    public static ItemStack getItemForSlot(String[] items, String fuel, int slot, ItemStack stack) {
        if (items.length > 0) {
            String string = StringUtils.join(items, ",").replaceAll("\\s+", "");

            if (slot == 0) {
                IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
                if (fluidHandler != null) {
                    for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
                        if (properties.getContents() != null) {
                            if (properties.getContents().getFluid().getName().toLowerCase().equals(fuel.toLowerCase())) {
                                return stack;
                            }
                        }
                    }
                }
            } else if (slot == 1) {
                IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack);
                if (fluidHandlerItem != null) {
                    if (fluidHandlerItem.getTankProperties()[0].getContents() == null)
                        return stack;
                } else return ItemStack.EMPTY;
            } else if (slot == 2) {
                string = string.substring(0, string.indexOf(","));
            } else if (slot == 3) {
                string = string.substring(string.indexOf(",") + 1, StringUtils.ordinalIndexOf(string, ",", 2));
            } else if (slot == 4) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 2) + 1, StringUtils.ordinalIndexOf(string, ",", 3));
            } else if (slot == 5) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 3) + 1, StringUtils.ordinalIndexOf(string, ",", 4));
            } else if (slot == 6) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 4) + 1, StringUtils.ordinalIndexOf(string, ",", 5));
            } else if (slot == 7) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 5) + 1, StringUtils.ordinalIndexOf(string, ",", 6));
            } else if (slot == 8) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 6) + 1, StringUtils.ordinalIndexOf(string, ",", 7));
            } else if (slot == 9) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 7) + 1, StringUtils.ordinalIndexOf(string, ",", 8));
            } else if (slot == 10) {
                string = string.substring(StringUtils.ordinalIndexOf(string, ",", 8) + 1);
            }

            if (!string.contains(",")) {
                ItemStack itemStack = getStack(string);
                if (itemStack.equals(ItemStack.EMPTY)) {
                    Constants.Log.getLogger("NetherUtils_Portal-Items").info(string + ", does not exist");
                } else return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Gets an {@link ItemStack} from a string
     *
     * @param string the input string from which to get the item
     * @return the {@link ItemStack} which is in the string, EMPTY if null
     */
    private static ItemStack getStack(String string) {
        int meta = -1;
        if (string.contains("|")) {
            String number = string.substring(string.indexOf("|") + 1);
            meta = Integer.parseInt(CharMatcher.digit().retainFrom(number));
        }
        Item item = Item.getByNameOrId(string.contains("|") ? string.substring(0, string.indexOf("|")) : string);
        if (item != null) {
            return new ItemStack(item, meta == -1 ? 1 : 2, meta == -1 ? 0 : meta);
        } else {
            Constants.Log.getLogger().debug("Portal Item '" + string + "', doesn't exist");
            return ItemStack.EMPTY;
        }
    }

    /**
     * Wrapper Class which implements IPortalMaster for deserialization
     */
    private static class PortalMaster implements IPortalMaster {

        BlockPos slavePos;
        BlockPos blockPos;
        boolean isActive;
        boolean isNether;
        int dimension;

        private PortalMaster(BlockPos slavePos, BlockPos blockPos, boolean isActive, int dimension, boolean isNether) {
            this.slavePos = slavePos;
            this.blockPos = blockPos;
            this.isActive = isActive;
            this.isNether = isNether;
            this.dimension = dimension;
        }

        @Override
        public void setSlavePos(BlockPos pos) {
            this.slavePos = pos;
        }

        @Override
        public BlockPos getSlavePos() {
            return this.slavePos;
        }

        @Override
        public void isActive(boolean isActive) {
            this.isActive = isActive;
        }

        @Override
        public boolean isActive() {
            return this.isActive;
        }

        @Override
        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        @Override
        public int getDimension() {
            return this.dimension;
        }

        @Override
        public boolean isNether() {
            return this.isNether;
        }
    }
}