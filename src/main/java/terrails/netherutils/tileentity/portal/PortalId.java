package terrails.netherutils.tileentity.portal;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.StringUtils;
import terrails.netherutils.Constants;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.config.ConfigHandler;

import java.util.*;

public class PortalId {

    public static final List<IPortalMaster> MASTER_LIST = Lists.newArrayList();

    public static boolean addPortal(IPortalMaster portal) {
        for (IPortalMaster master : MASTER_LIST) {
            if (master.getPos().equals(portal.getPos()))
                return false;
        }
        MASTER_LIST.add(portal);
        return true;
    }
    public static boolean removePortal(IPortalMaster portal) {
        boolean value = false;
        Iterator<IPortalMaster> iterator = MASTER_LIST.iterator();
        while (iterator.hasNext()) {
            IPortalMaster master = iterator.next();

            if (master.getPos().equals(portal.getPos())) {
                iterator.remove();
                value = true;
            }
        }
        return value;
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
                if (FluidUtil.getFluidHandler(stack) != null) {
                    return stack;
                }
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
