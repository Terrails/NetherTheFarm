package terrails.netherutils.api.capabilities;

public interface IFirstSpawn {

    boolean hasStartingItems();
    void hasStartingItems(boolean hasItems);

    boolean hasStartingEffects();
    void hasStartingEffects(boolean hasEffects);

    boolean isNew();
    void isNew(boolean isNew);
}
