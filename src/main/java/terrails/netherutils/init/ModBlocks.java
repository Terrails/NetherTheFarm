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

    public static List<Block> blocks = Lists.newArrayList();

    public static final Block SPAWN_POINT;
    public static final Block OBELISK;
    public static final Block TANK;
    public static final Block SOUL_SOIL;

    public static final Block PLANKS;
    public static final Block LOG;
    public static final Block LEAVES;
    public static final Block SAPLING;

    @GameRegistry.ObjectHolder("netherutils:nether_portal")
    public static final Block PORTAL_NETHER_MASTER;
    public static final Block PORTAL_NETHER_SLAVE;

    @GameRegistry.ObjectHolder("netherutils:end_portal")
    public static final Block PORTAL_END_MASTER;
    public static final Block PORTAL_END_SLAVE;

    public static final Block PEDESTAL;

    public static final Block END_PORTAL_OVERRIDDEN;

    static {
        SPAWN_POINT = add(new BlockSpawnPoint("spawn_point"));
        TANK = add(new BlockTank("tank"));
        OBELISK = add(new BlockObelisk("obelisk"));
        SOUL_SOIL = add(new BlockSoulSoil("soul_soil"));

        PORTAL_NETHER_MASTER = add(new BlockPortal("nether_portal"));
        PORTAL_NETHER_SLAVE = add(new BlockPortalSlave("nether_portal_slave"));

        PORTAL_END_MASTER = add(new terrails.netherutils.blocks.portal.end.BlockPortal("end_portal"));
        PORTAL_END_SLAVE = add(new terrails.netherutils.blocks.portal.end.BlockPortalSlave("end_portal_slave"));

        PEDESTAL = add(new BlockPedestal("pedestal"));

        END_PORTAL_OVERRIDDEN = new BlockPortalOverride();

        PLANKS = add(new BlockNTFPlanks("planks"));
        LOG = add(new BlockNTFLog("log"));
        LEAVES = add(new BlockNTFLeaf("leaves"));
        SAPLING = add(new BlockNTFSapling("sapling"));
    }

    public static <T extends Block> T add(T block) {
        blocks.add(block);
        return block;
    }
}