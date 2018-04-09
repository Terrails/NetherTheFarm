package terrails.netherutils.blocks.wood;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.blocks.wood.WoodType;

import java.util.Objects;

public class ItemBlockLog extends ItemBlock {

    public ItemBlockLog(Block block) {
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
        return "tile." + NetherUtils.MOD_ID + ".log_" + WoodType.byMetadata(stack.getMetadata());
    }
}
