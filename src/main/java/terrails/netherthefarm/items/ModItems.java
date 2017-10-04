package terrails.netherthefarm.items;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    public static List<Item> itemList = new ArrayList<>();

    public static void init() {

    }

    public static Item register(Item item) {
        itemList.add(item);
        return item;
    }
}
