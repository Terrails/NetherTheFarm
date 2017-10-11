package terrails.netherthefarm.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import terrails.netherthefarm.init.ModBlocks;

public class CreativeTabNTF extends CreativeTabs {

    public CreativeTabNTF(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.OBELISK);
    }
}
