package terrails.netherutils.api.capabilities;

import net.minecraft.util.math.BlockPos;

/**
 * A capability to provide the player info for the obelisk
 *
 * @author Terrails
 */

public interface IObelisk {

    /**
     * Checks if an obelisk exists
     *
     * @return true if it exists else false
     */
    boolean hasObelisk();

    /**
     * Get the obelisk blockPos
     *
     * @return the obelisk pos
     */
    BlockPos getObeliskPos();

    /**
     * Set the obelisk blockPos
     *
     * @param pos {@link BlockPos} of the obelisk
     */
    void setObeliskPos(BlockPos pos);

    /**
     * Get the dimension of the obelisk block.
     *
     * @return The dimension of obelisk
     */
    int getObeliskDim();

    /**
     * Set the dimension of the obelisk block.
     *
     * @param dim The obelisk dimension
     */
    void setObeliskDim(int dim);
}
