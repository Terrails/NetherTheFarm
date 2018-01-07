package terrails.netherutils.init;

import terrails.netherutils.api.advancements.PortalItemTrigger;
import terrails.terracore.registry.TerraRegistry;

public class ModAdvancements {

    public static PortalItemTrigger PORTAL_ITEM_TRIGGER;

    public static void init() {
        PORTAL_ITEM_TRIGGER = (PortalItemTrigger) TerraRegistry.registerAdvancementTrigger(new PortalItemTrigger());
    }
}
