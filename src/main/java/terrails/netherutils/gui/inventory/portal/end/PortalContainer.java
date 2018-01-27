package terrails.netherutils.gui.inventory.portal.end;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import terrails.netherutils.blocks.portal.end.TileEntityPortalMaster;

public class PortalContainer extends Container {

    private TileEntityPortalMaster tile;

    public PortalContainer(IInventory playerInventory, TileEntityPortalMaster tile) {
        this.tile = tile;

        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 9; col++){
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for(int col = 0; col < 9; col++){
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    private void addOwnSlots() {
        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        assert itemHandler != null;
        int slotIndex = 0;
        int xPos = 62;
        int yPos = 17;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (i == 0) {
                addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, 34, 23));
                slotIndex++;
            } else if (i == 1) {
                addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, 34, 47));
                slotIndex++;
            } else if (i <= 4) {
                addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, xPos, 17));
                slotIndex++;
                xPos += 18;
                if (i == 4) xPos = 62;
            } else if (i <= 7) {
                if (i == 6) addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, xPos, yPos + 18));
                else addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, xPos, yPos + 18));
                slotIndex++;
                xPos += 18;
                if (i == 7) xPos = 62;
            } else if (i <= 10) {
                addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, xPos, yPos + 36));
                slotIndex++;
                xPos += 18;
                if (i == 10) xPos = 62;
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.canInteractWith(playerIn);
    }
}