package terrails.netherutils.items.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCertainItem extends SlotItemHandler {

    private final ItemStack itemStack;

    public SlotCertainItem(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemStack itemStack) {
        super(itemHandler, index, xPosition, yPosition);
        this.itemStack = itemStack;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return super.isItemValid(stack);
       // return stack.getMetadata() == this.itemStack.getMetadata() && stack.getUnlocalizedName().equals(this.itemStack.getUnlocalizedName());
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return super.getItemStackLimit(stack);
    }


}
