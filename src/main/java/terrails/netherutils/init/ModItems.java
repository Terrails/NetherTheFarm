package terrails.netherutils.init;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import terrails.netherutils.items.ItemForbiddenFruit;
import terrails.terracore.registry.newest.ItemRegistry;

public class ModItems extends ItemRegistry {

    public static Item FORBIDDEN_FRUIT;

    public static void init() {
        itemList = Lists.newArrayList();
        FORBIDDEN_FRUIT = add(new ItemForbiddenFruit("forbidden_fruit"));
    }
}
