package terrails.netherutils.tileentity.portal;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.StringUtils;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.world.data.CustomWorldData;

import java.util.*;

public class PortalRegistry {

   public static List<IPortalMaster> LIST = Lists.newArrayList();

    public static boolean addPortal(IPortalMaster portal, World world) {
        for (IPortalMaster master : LIST) {
            if (master.getBlockPos().equals(portal.getBlockPos()))
                return false;
        }
        LIST.add(portal);
        CustomWorldData.get(world).markDirty();
        return true;
    }
    public static boolean removePortal(IPortalMaster portal, World world) {
        boolean value = false;
        Iterator<IPortalMaster> iterator = LIST.iterator();
        while (iterator.hasNext()) {
            IPortalMaster master = iterator.next();

            if (master.getBlockPos().equals(portal.getBlockPos()) || (portal.getSlavePos() != BlockPos.ORIGIN && portal.getSlavePos().equals(master.getBlockPos()))) {
                iterator.remove();
                value = true;
            }
        }
        if (CustomWorldData.get(world) != null) {
            CustomWorldData.get(world).markDirty();
        }
        return value;
    }

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
            boolean shouldAdd = true;
            int index1 = 0;
            for (IPortalMaster master1 : LIST) {
                index1++;
                if (index != index1 && master.getBlockPos().equals(master1.getBlockPos()))
                    shouldAdd = false;
            }
            if (shouldAdd)
                tagList.appendTag(nbtTag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Portals", tagList);
        return nbt;
    }
    public static List<IPortalMaster> deserializeNBT(NBTTagCompound nbt) {
        List<IPortalMaster> list = Lists.newArrayList();
        NBTTagList tagList = nbt.getTagList("Portals", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbtTag = tagList.getCompoundTagAt(i);
            boolean isActive = nbtTag.getBoolean("isActive");

            BlockPos slavePos = BlockPos.ORIGIN;
            if (nbtTag.getInteger("ySlave") != 0) {
                slavePos = new BlockPos(nbtTag.getInteger("xSlave"), nbtTag.getInteger("ySlave"), nbtTag.getInteger("zSlave"));
            }
            
            BlockPos blockPos = BlockPos.ORIGIN;
            if (nbtTag.getInteger("yPos") != 0) {
                blockPos = new BlockPos(nbtTag.getInteger("xPos"), nbtTag.getInteger("yPos"), nbtTag.getInteger("zPos"));
            }

            if (blockPos != BlockPos.ORIGIN)
                list.add(new PortalMaster(slavePos, blockPos, isActive));
        }
        return list;
    }

    private static class PortalMaster implements IPortalMaster {

        BlockPos slavePos;
        BlockPos blockPos;
        boolean isActive;

        private PortalMaster(BlockPos slavePos, BlockPos blockPos, boolean isActive) {
            this.slavePos = slavePos;
            this.blockPos = blockPos;
            this.isActive = isActive;
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
    }

    public static ItemStack getItemForSlot(int slot, ItemStack stack) {
        if (ConfigHandler.portalItems.length > 0) {
            String string = StringUtils.join(ConfigHandler.portalItems, ",").replaceAll("\\s+", "");

            if (slot == 0) {
                IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
                if (fluidHandler != null) {
                    for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
                        if (properties.getContents() != null) {
                            if (properties.getContents().getFluid().getName().toLowerCase().equals(ConfigHandler.portalFuel.toLowerCase())) {
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
                    Constants.Log.info(string + ", does not exist");
                } else return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
    private static ItemStack getStack(String string) {
        int meta = 0;
        if (string.contains("|")) {
            string = string.substring(string.indexOf("|") + 1);
            meta = Integer.parseInt(CharMatcher.digit().retainFrom(string));
        }
        Item item = Item.getByNameOrId(string.contains("|") ? string.substring(0, string.indexOf("|")) : string);
        if (item != null) {
            return new ItemStack(item, 1, meta);
        } else return ItemStack.EMPTY;
    }
}