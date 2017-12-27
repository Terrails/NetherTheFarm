package terrails.netherutils.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import terrails.netherutils.blocks.wood.WoodType;

public class ItemBlockSapling extends ItemBlock {

    public ItemBlockSapling(Block block) {
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
        return "tile.sapling_" + WoodType.byMetadata(stack.getMetadata());
    }
}
