package terrails.netherutils.init;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import terrails.netherutils.blocks.*;
import terrails.netherutils.blocks.obelisk.BlockObelisk;
import terrails.netherutils.blocks.pedestal.BlockPedestal;
import terrails.netherutils.blocks.portal.end.BlockPortalOverride;
import terrails.netherutils.blocks.portal.nether.BlockPortal;
import terrails.netherutils.blocks.portal.nether.BlockPortalSlave;
import terrails.netherutils.blocks.tank.BlockTank;
import terrails.netherutils.blocks.wood.BlockNTFLeaf;
import terrails.netherutils.blocks.wood.BlockNTFLog;
import terrails.netherutils.blocks.wood.BlockNTFPlanks;
import terrails.netherutils.blocks.wood.BlockNTFSapling;

import java.util.List;

public class ModBlocks {

    private static List<Block> blocks = Lists.newArrayList();

    public static Block SPAWN_POINT;
    public static Block OBELISK;
    public static Block TANK;
    public static Block SOUL_SOIL;

    public static Block PLANKS;
    public static Block LOG;
    public static Block LEAVES;
    public static Block SAPLING;

    @GameRegistry.ObjectHolder("netherutils:nether_portal")
    public static Block PORTAL_NETHER_MASTER;
    public static Block PORTAL_NETHER_SLAVE;

    @GameRegistry.ObjectHolder("netherutils:end_portal")
    public static Block PORTAL_END_MASTER;
    public static Block PORTAL_END_SLAVE;

    public static Block PEDESTAL;

    public static Block END_PORTAL_OVERRIDDEN;

    protected static void init() {
        SPAWN_POINT = add(new BlockSpawnPoint("spawn_point"));
        TANK = add(new BlockTank("tank"));
        OBELISK = add(new BlockObelisk("obelisk"));
        SOUL_SOIL = add(new BlockSoulSoil("soul_soil"));

        PORTAL_NETHER_MASTER = add(new BlockPortal("nether_portal"));
        PORTAL_NETHER_SLAVE = add(new BlockPortalSlave("nether_portal_slave"));

        PORTAL_END_MASTER = add(new terrails.netherutils.blocks.portal.end.BlockPortal("end_portal"));
        PORTAL_END_SLAVE = add(new terrails.netherutils.blocks.portal.end.BlockPortalSlave("end_portal_slave"));

        PEDESTAL = add(new BlockPedestal("pedestal"));

        END_PORTAL_OVERRIDDEN = add(new BlockPortalOverride());

        PLANKS = add(new BlockNTFPlanks("planks"));
        LOG = add(new BlockNTFLog("log"));
        LEAVES = add(new BlockNTFLeaf("leaves"));
        SAPLING = add(new BlockNTFSapling("sapling"));
    }

    public static <T extends Block> T add(T block) {
        blocks.add(block);
        return block;
    }

    public static Block[] get() {
        return blocks.toArray(new Block[blocks.size()]);
    }
}