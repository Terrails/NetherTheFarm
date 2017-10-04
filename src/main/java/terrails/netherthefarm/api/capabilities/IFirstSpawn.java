package terrails.netherthefarm.api.capabilities;

/**
 * A capabilities to provide the first spawn of a player.
 *
 * @author Terrails
 */

public interface IFirstSpawn {

    /**
     * Get 0 or 1 depending if player has a spawn point.
     *
     * @return The boolean of has first spawn (0 == False, 1 == True)
     */
    int hasObelisk();

    /**
     * Set 0 or 1 depending if player has a spawn point.
     *
     * @param hasSpawn The boolean of has first spawn (0 == False, 1 == True)
     */
    void hasObelisk(int hasSpawn);

    /**
     * Get X axis of the obelisk block.
     *
     * @return The X axis of Obelisk
     */
    int posX();

    /**
     * Set X axis of the obelisk block.
     *
     * @param posX The X axis
     */
    void setPosX(int posX);

    /**
     * Get Y axis of the obelisk block.
     *
     * @return The Y axis of Obelisk
     */
    int posY();

    /**
     * Set Y axis of the obelisk block.
     *
     * @param posY The Y axis
     */
    void setPosY(int posY);

    /**
     * Get Z axis of the obelisk block.
     *
     * @return The Z axis of Obelisk
     */
    int posZ();

    /**
     * Set Z axis of the obelisk block.
     *
     * @param posZ The Z axis
     */
    void setPosZ(int posZ);

    /**
     * Get dimension of the obelisk block.
     *
     * @return The dimension of obelisk
     */
    int obeliskDim();

    /**
     * Set dimension of the obelisk block.
     *
     * @param dim The obelisk dimension
     */
    void setObeliskDim(int dim);


    //* Starting Features: Items and Potion Effects *\\
    /**
     * Get 0 or 1 depending if player has been given starting items.
     *
     * @return The boolean of starting features (0 == False, 1 == True)
     */
    int hasStartingFeatures();

    /**
     * Set 0 or 1 depending if player has been given starting items.
     *
     * @param hasItems the boolean of has starting features (0 == False, 1 == True)
     */
    void hasStartingFeatures(int hasItems);

    /**
     * Get player dimension before death
     *
     * @return The player dimension before death
     */
    int oldPlayerDimension();

    /**
     * Set player dimension before death
     *
     * @param dim the player dimension before death
     */
    void oldPlayerDimension(int dim);




/*
    NBTTagCompound nbtTag();
    void setNbtTag(NBTTagCompound nbt);



    boolean hasObeliskInDim(int dim);
    void setHasObeliskDim(boolean hasObeliskDim, int inDim);

    int posDimX(int dim);
    void setPosDimX(int posX, int dim);
    int posDimY(int dim);
    void setPosDimY(int posY, int dim);
    int posDimZ(int dim);
    void setPosDimZ(int posZ, int dim);
    */
}
