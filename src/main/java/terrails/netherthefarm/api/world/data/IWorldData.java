package terrails.netherthefarm.api.world.data;

public interface IWorldData {

    /**
     * Get the X axis of the spawn point
     *
     * @return The X axis of the spawn point
     */
    int posX();

    /**
     * Set the X axis of the spawn point
     *
     * @param posX The X axis of the spawn point
     */
    void setPosX(int posX);


    /**
     * Get the Y axis of the spawn point
     *
     * @return The Y axis of the spawn point
     */
    int posY();

    /**
     * Set the Y axis of the spawn point
     *
     * @param posY The Y axis of the spawn point
     */
    void setPosY(int posY);


    /**
     * Get the Z axis of the spawn point
     *
     * @return The Z axis of the spawn point
     */
    int posZ();

    /**
     * Set the Z axis of the spawn point
     *
     * @param posZ The Z axis of the spawn point
     */
    void setPosZ(int posZ);

    /**
     * Get the dimension of the spawn point
     *
     * @return The dimension of the spawn point
     */
    int spawnPointDim();

    /**
     * Get the dimension of the spawn point
     *
     * @param pointDim The dimension of the spawn point
     */
    void spawnPointDim(int pointDim);

    /**
     * Checks if the world has a spawn point
     *
     * @return the boolean value
     */
    boolean hasSpawnPoint();

    /**
     * Set the boolean if world has a spawn point
     *
     * @param hasSpawn
     */
    void hasSpawnPoint(boolean hasSpawn);
}
