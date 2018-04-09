package terrails.netherutils.init;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.items.ItemDebugTool;
import terrails.netherutils.items.ItemForbiddenFruit;

import java.util.List;

public class ModItems {

    public static List<Item> items = Lists.newArrayList();

    public static final Item FORBIDDEN_FRUIT;
    public static Item DEBUG_TOOL;

    static {
        FORBIDDEN_FRUIT = add(new ItemForbiddenFruit("forbidden_fruit"));
        if (ConfigHandler.portalDebugTool) {
            DEBUG_TOOL = add(new ItemDebugTool("debug_tool"));
        }
    }

    public static <T extends Item> T add(T item) {
        items.add(item);
        return item;
    }

}
