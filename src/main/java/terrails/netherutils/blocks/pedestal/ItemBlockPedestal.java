package terrails.netherutils.blocks.pedestal;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;

import java.util.Objects;

public class ItemBlockPedestal extends ItemBlock {

    public ItemBlockPedestal(Block block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + NetherUtils.MOD_ID + ".pedestal_" + BlockPedestal.Type.byMetadata(stack.getMetadata());
    }
}