package terrails.netherutils.blocks.portal.nether;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import terrails.netherutils.config.ConfigHandler;

import javax.annotation.Nullable;
import java.util.*;

public class ItemBlockPortal extends ItemBlock {

    public ItemBlockPortal(Block block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack inputStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound compound = inputStack.getTagCompound();
        boolean info = false;

        if (ConfigHandler.portalKeepFluid) {
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(inputStack);
            if (fluidHandler != null) {
                IFluidTankProperties properties = fluidHandler.getTankProperties()[0];
                FluidStack fluidStack = properties.getContents();

                if (fluidStack != null) {
                    info = true;
                    if (GuiScreen.isShiftKeyDown() || (compound == null || !compound.hasKey("Inventory"))) {
                        String GREEN = TextFormatting.GREEN + "";
                        String GOLD = TextFormatting.GOLD + "";
                        String GRAY = TextFormatting.GRAY + "";
                        String AQUA = TextFormatting.AQUA + "";
                        tooltip.add(GREEN + fluidStack.amount + GOLD + "/" + GREEN + "" + properties.getCapacity() + GRAY + " (" + AQUA + fluidStack.getLocalizedName() + GRAY + ")");
                    } else if (!GuiScreen.isShiftKeyDown()) tooltip.add("Press shift for more info...");
                }
            }
        }

        if (ConfigHandler.portalKeepInventory) {
            if (compound == null || !compound.hasKey("Inventory"))
                return;

            if (!info && !GuiScreen.isShiftKeyDown())
                tooltip.add("Press shift for more info...");

            if (GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown() && info) {
                tooltip.add("Press ctrl for inventory info...");
                return;
            }

            if ((GuiScreen.isShiftKeyDown() && !info) || (GuiScreen.isCtrlKeyDown() && GuiScreen.isCtrlKeyDown() && info)) {
                compound = compound.getCompoundTag("Inventory");

                // Pretty much the same as ItemStackHandler#deserializeNBT
                List<ItemStack> stacks = NonNullList.withSize(11, ItemStack.EMPTY);
                stacks = NonNullList.withSize((compound.hasKey("Size", Constants.NBT.TAG_INT) ? compound.getInteger("Size") : stacks.size()), ItemStack.EMPTY);
                NBTTagList tagList = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                    int slot = itemTags.getInteger("Slot");
                    if (slot >= 0 && slot < stacks.size()) {
                        stacks.set(slot, new ItemStack(itemTags));
                    }
                }

                List<ItemStack> newStacks = Lists.newArrayList();
                for (int index = 0; index < stacks.size(); index++) {
                    ItemStack stack = stacks.get(index).copy();
                    ListIterator<ItemStack> iterator = stacks.listIterator();
                    while (iterator.hasNext()) {
                        int index1 = iterator.nextIndex();
                        ItemStack itemStack = iterator.next();
                        if (index != index1 && stack.getUnlocalizedName().equals(itemStack.getUnlocalizedName())) {
                            newStacks.removeIf(itemStack1 -> itemStack1.getUnlocalizedName().equals(itemStack.getUnlocalizedName()));
                            stack.grow(itemStack.getCount());
                        }
                    }
                    newStacks.add(stack);
                }

                for (ItemStack itemStack : newStacks) {
                    if (!itemStack.isEmpty()) {
                        tooltip.add(itemStack.getCount() + "x " + itemStack.getDisplayName());
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, ConfigHandler.portalCapacity);
    }
}