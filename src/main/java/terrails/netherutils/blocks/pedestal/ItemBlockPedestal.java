package terrails.netherutils.blocks.pedestal;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import terrails.netherutils.Constants;

public class ItemBlockPedestal extends ItemBlock {

    public ItemBlockPedestal(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + Constants.MOD_ID + ".pedestal_" + BlockPedestal.Type.byMetadata(stack.getMetadata());
    }
}