package terrails.netherutils.init;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import terrails.netherutils.blocks.*;
import terrails.netherutils.blocks.wood.BlockNTFLeaf;
import terrails.netherutils.blocks.wood.BlockNTFLog;
import terrails.netherutils.blocks.wood.BlockNTFPlanks;
import terrails.netherutils.blocks.wood.BlockNTFSapling;
import terrails.terracore.registry.newest.BlockRegistry;

public class ModBlocks extends BlockRegistry {

    public static Block SPAWN_POINT;
    public static Block OBELISK;
    public static Block TANK;
    public static Block SOUL_SOIL;

    public static Block PLANKS;
    public static Block LOG;
    public static Block LEAVES;
    public static Block SAPLING;

    @GameRegistry.ObjectHolder("netherutils:portal")
    public static Block PORTAL_MASTER;
    public static Block PORTAL_SLAVE;

    public static Block PEDESTAL;

    protected static void init() {
        blockList = Lists.newArrayList();
        SPAWN_POINT = add(new BlockSpawnPoint("spawn_point"));
        TANK = add(new BlockTank("tank"));
        OBELISK = add(new BlockObelisk("obelisk"));
        SOUL_SOIL = add(new BlockSoulSoil("soul_soil"));

        PORTAL_MASTER = add(new BlockPortal("portal"));
        PORTAL_SLAVE = add(new BlockPortalSlave("portal_slave"));

        PEDESTAL = add(new BlockPedestal("pedestal"));

        PLANKS = add(new BlockNTFPlanks("planks"));
        LOG = add(new BlockNTFLog("log"));
        LEAVES = add(new BlockNTFLeaf("leaves"));
        SAPLING = add(new BlockNTFSapling("sapling"));
    }
}
