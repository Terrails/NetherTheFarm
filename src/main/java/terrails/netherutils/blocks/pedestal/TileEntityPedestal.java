package terrails.netherutils.blocks.pedestal;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import terrails.terracore.block.tile.TileEntityBase;

public class TileEntityPedestal extends TileEntityBase {

    private ItemStack stack = ItemStack.EMPTY;

    public void setStack(ItemStack stack) {
        this.stack = stack;
        world.notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        stack = compound.hasKey("Item") ? new ItemStack(compound.getCompoundTag("Item")) : ItemStack.EMPTY;
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Item", stack.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(compound);
    }
}
