package terrails.netherutils.entity.capabilities.firstspawn;

import terrails.netherutils.api.capabilities.IFirstSpawn;

public class FirstSpawn implements IFirstSpawn {

    private boolean startItems;
    private boolean startEffects;
    private boolean isNew = true;

    @Override
    public boolean hasStartingItems() {
        return this.startItems;
    }

    @Override
    public void hasStartingItems(boolean hasItems) {
        this.startItems = hasItems;
    }

    @Override
    public boolean hasStartingEffects() {
        return this.startEffects;
    }

    @Override
    public void hasStartingEffects(boolean hasEffects) {
        this.startEffects = hasEffects;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @Override
    public void isNew(boolean isNew) {
        this.isNew = isNew;
    }
}
