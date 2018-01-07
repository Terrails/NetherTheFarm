package terrails.netherutils.entity.capabilities;

import terrails.netherutils.api.capabilities.IPortalItem;

public class PortalItem implements IPortalItem {

    private boolean hasCrafted;

    @Override
    public boolean hasCrafted() {
        return this.hasCrafted;
    }

    @Override
    public void hasCrafted(boolean hasCrafted) {
        this.hasCrafted = hasCrafted;
    }
}
