package terrails.netherutils.api.capabilities;

/**
 * A capability to provide the player info for the first spawn
 *
 * @author Terrails
 */

public interface IFirstSpawn {

    /**
     * True or False depending if player has been given starting items.
     *
     * @return The boolean of starting item (False, True)
     */
    boolean hasStartingItems();

    /**
     * Set True or False depending if player has been given starting items.
     *
     * @param hasItems the boolean of has starting items (False, True)
     */
    void hasStartingItems(boolean hasItems);

    /**
     * True or False depending if player has been given starting items.
     *
     * @return The boolean of starting item (False, True)
     */
    boolean hasStartingEffects();

    /**
     * Set True or False depending if player has been given starting effects.
     *
     * @param hasEffects the boolean of has starting effects (False, True)
     */
    void hasStartingEffects(boolean hasEffects);


    /**
     * True or False depending if the player has ever joined the world
     *
     * @return The boolean of first join (False, True)
     */
    boolean isNew();

    /**
     * set the True or False depending if the player has ever joined the world
     *
     * @param isNew the boolean of first join (False, True)
     */
    void isNew(boolean isNew);
}
