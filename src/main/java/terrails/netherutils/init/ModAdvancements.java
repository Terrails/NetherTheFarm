package terrails.netherutils.init;

import net.minecraft.advancements.CriteriaTriggers;
import terrails.netherutils.api.advancements.PortalItemTrigger;

public class ModAdvancements {

    public static PortalItemTrigger PORTAL_ITEM_TRIGGER;

    public static void init() {
        PORTAL_ITEM_TRIGGER = CriteriaTriggers.register(new PortalItemTrigger());
    }
}
