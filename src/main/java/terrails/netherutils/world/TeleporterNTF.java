package terrails.netherutils.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.helper.BlockHelper;

import java.util.Objects;
import java.util.Random;

public class TeleporterNTF extends Teleporter {

    private final WorldServer worldServer;
    private BlockPos pos;

    public TeleporterNTF(WorldServer worldIn, BlockPos pos) {
        super(worldIn);
        this.worldServer = world;
        this.pos = pos;
    }

    public static void teleport(EntityPlayerMP player, int dimension, BlockPos pos, boolean usePointGen) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int oldDimension = player.dimension;
        WorldServer world = Objects.requireNonNull(player.getEntityWorld().getMinecraftServer()).getWorld(dimension);
        PlayerList playerList = Objects.requireNonNull(world.getMinecraftServer()).getPlayerList();
        IWorldData data = CustomWorldData.get(world);

        if (player.dimension != dimension) {
            // Compatibility with EntityTravelToDimensionEvent
            EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(player, dimension);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                playerList.transferPlayerToDimension(player, dimension, new TeleporterNTF(world, pos));
            }
        }
     //   player.setPositionAndUpdate(x + 0.5, y, z + 0.5);

        if (oldDimension == 1) {
            world.updateEntityWithOptionalForce(player, false);
        }

        if (usePointGen) {
            if (isWorldType(player.getEntityWorld(), "NetherSurvival") || isWorldType(player.getEntityWorld(), "BIOMESOP")) {
                if (data != null && !data.hasSpawnPoint() && ConfigHandler.pointRespawn) {
                    if (!(world.getBlockState(data.getPointPos()) == ModBlocks.SPAWN_POINT.getDefaultState())) {
                        genLavaPlatform(player, world, data);
                    }
                    if (!(world.getBlockState(data.getPointPos()) == ModBlocks.SPAWN_POINT.getDefaultState())) {
                        genSpot(player, world, data);
                    }
                    if (!(world.getBlockState(data.getPointPos()) == ModBlocks.SPAWN_POINT.getDefaultState())) {
                        genLastResort(player, world, data);
                    }
                }
            }
        }
    }


    private static void genLavaPlatform(EntityPlayerMP player, World world, IWorldData data) {
        BlockPos netherTop = world.getTopSolidOrLiquidBlock(player.getPosition());
        for (int y = netherTop.getY(); y > 25; y--) {
            BlockPos blockPos = new BlockPos(netherTop.getX(), y, netherTop.getZ());
            if (world.getBlockState(blockPos).equals(Blocks.LAVA.getDefaultState()) /*&& BlockHelper.check(1, 0, blockPos, world, Blocks.LAVA.getDefaultState(), false, false)*/) {
                BlockHelper.fill(2, blockPos, world, Blocks.NETHER_BRICK.getDefaultState(), false, true);

                BlockHelper.fill(2, blockPos.up(), world, Blocks.AIR.getDefaultState(), false, true);
                BlockHelper.fill(2, blockPos.up(2), world, Blocks.AIR.getDefaultState(), false, true);
                BlockHelper.fill(2, blockPos.up(3), world, Blocks.AIR.getDefaultState(), false, true);

                player.setPositionAndUpdate(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);

                if (BlockHelper.checkAny(1, blockPos.up(4), world, Blocks.LAVA.getDefaultState(), false, false)) {
                    BlockHelper.fill(2, blockPos.up(4), world, Blocks.NETHER_BRICK.getDefaultState(), false, true);
                }
                if (BlockHelper.checkAny(1, blockPos, world, Blocks.LAVA.getDefaultState(), true, false) || BlockHelper.checkAny(1, blockPos.up(), world, Blocks.LAVA.getDefaultState(), true, false) || BlockHelper.checkAny(1, blockPos.up(2), world, Blocks.LAVA.getDefaultState(), true, false)) {
                    BlockHelper.fill(2, blockPos, world, Blocks.NETHER_BRICK.getDefaultState(), true, true);
                    BlockHelper.fill(2, blockPos.up(), world, Blocks.NETHER_BRICK.getDefaultState(), true, true);
                    BlockHelper.fill(2, blockPos.up(2), world, Blocks.NETHER_BRICK.getDefaultState(), true, true);
                }

                world.setBlockState(player.getPosition().down(), ModBlocks.SPAWN_POINT.getDefaultState());
                data.setPointPos(player.getPosition().down());
                break;
            }
        }
    }
    private static void genSpot(EntityPlayerMP player, World world, IWorldData data) {
        BlockPos pos = player.getPosition();
        for (int y = 110; y > 35 && y < 120; y--) {
            BlockPos blockPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (!world.getBlockState(blockPos).equals(Blocks.AIR.getDefaultState()) && world.getBlockState(blockPos.up()).equals(Blocks.AIR.getDefaultState()) && world.getBlockState(blockPos.up(2)).equals(Blocks.AIR.getDefaultState())) {
                player.setPositionAndUpdate(pos.getX() + 0.5, y + 1 , pos.getZ() + 0.5);

                BlockPos playerPosition = player.getPosition();

                BlockHelper.fill(2, playerPosition.down(), world, Blocks.NETHER_BRICK.getDefaultState(), false, true);

                BlockHelper.fill(2, playerPosition.down(3), world, Blocks.NETHERRACK.getDefaultState(), false, true);
                BlockHelper.fill(2, playerPosition.down(2), world, Blocks.NETHERRACK.getDefaultState(), false, true);

                BlockHelper.fill(2, playerPosition, world, Blocks.AIR.getDefaultState(), false, true);
                BlockHelper.fill(2, playerPosition.up(), world, Blocks.AIR.getDefaultState(), false, true);
                BlockHelper.fill(2, playerPosition.up(2), world, Blocks.AIR.getDefaultState(), false, true);

                world.setBlockState(playerPosition.down(), ModBlocks.SPAWN_POINT.getDefaultState());
                data.setPointPos(playerPosition.down());
                break;
            }
        }
    }
    private static void genLastResort(EntityPlayerMP player, World world, IWorldData data) {
        Random random = new Random();
        int randomY = random.nextInt(80 + 1 - 40) + 50;

        player.setPositionAndUpdate(player.getPosition().getX() + 0.5, randomY, player.getPosition().getZ() + 0.5);

        BlockHelper.fill(2, player.getPosition().down(), world, Blocks.NETHER_BRICK.getDefaultState(), false, true);

        BlockHelper.fill(2, player.getPosition(), world, Blocks.AIR.getDefaultState(), false, true);
        BlockHelper.fill(2, player.getPosition().up(), world, Blocks.AIR.getDefaultState(), false, true);
        BlockHelper.fill(2, player.getPosition().up(2), world, Blocks.AIR.getDefaultState(), false, true);

        BlockHelper.fill(3, player.getPosition().up(3), world, Blocks.NETHER_BRICK.getDefaultState(), false, true);

        BlockHelper.fill(3, player.getPosition(), world, Blocks.NETHER_BRICK.getDefaultState(), true, true);
        BlockHelper.fill(3, player.getPosition().up(), world, Blocks.NETHER_BRICK.getDefaultState(), true, true);
        BlockHelper.fill(3, player.getPosition().up(2), world, Blocks.NETHER_BRICK.getDefaultState(), true, true);

        world.setBlockState(player.getPosition().down(), ModBlocks.SPAWN_POINT.getDefaultState());
        data.setPointPos(player.getPosition().down());
    }

    private static boolean isWorldType(World world, String string) {
        return world.getWorldType().getName().equals(string);
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        this.worldServer.getBlockState(new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ()));

        entity.setPosition(this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5);
        entity.motionX = 0.0f;
        entity.motionY = 0.0f;
        entity.motionZ = 0.0f;
    }
}
