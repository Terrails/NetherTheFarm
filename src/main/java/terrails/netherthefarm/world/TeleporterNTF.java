package terrails.netherthefarm.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import terrails.netherthefarm.init.ModBlocks;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.world.data.CustomWorldData;
import terrails.terracore.helper.BlockHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class TeleporterNTF extends Teleporter {

    public TeleporterNTF(WorldServer world, BlockPos blockPos) {
        super(world);
        this.worldServer = world;
        this.blockPos = blockPos;
    }

    private final WorldServer worldServer;
    private BlockPos blockPos;
    
    public static void teleportToDimension(EntityPlayerMP playerMP, int dimension, BlockPos blockPos) {
        if (blockPos != null) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            MinecraftServer server = playerMP.getEntityWorld().getMinecraftServer();
            WorldServer world = server.getWorld(dimension);
            PlayerList playerList = world.getMinecraftServer().getPlayerList();
            CustomWorldData worldData = CustomWorldData.get(playerMP.getEntityWorld());

            if (dimension != 0)
                playerList.transferPlayerToDimension(playerMP, dimension, new TeleporterNTF(world, blockPos));
            playerMP.setPositionAndUpdate(x + 0.5, y, z + 0.5);

            BlockPos worldDataPos = new BlockPos(worldData.posX(), worldData.posY(), worldData.posZ());
            if (!worldData.hasSpawnPoint() && ConfigHandler.spawnPoint) {
                if (playerMP.getEntityWorld().getWorldType().getName().equals("NetherSurvival") || playerMP.getEntityWorld().getWorldType().getName().equals("BIOMESOP")) {
                    if (!worldData.hasSpawnPoint() && world.getBlockState(worldDataPos) != ModBlocks.SPAWN_POINT.getDefaultState()) {
                        lavaPlatform(playerMP, world);
                    }
                    if (!worldData.hasSpawnPoint() && world.getBlockState(worldDataPos) != ModBlocks.SPAWN_POINT.getDefaultState()) {
                        findSpot(playerMP, world);
                    }
                    if (!worldData.hasSpawnPoint() && world.getBlockState(worldDataPos) != ModBlocks.SPAWN_POINT.getDefaultState()) {
                        lastResort(playerMP, world);
                    }
                }
            }
        }
    }
    private static void lavaPlatform(EntityPlayer player, World world) {
        BlockPos netherTop = world.getTopSolidOrLiquidBlock(player.getPosition());
        for (int y = netherTop.getY(); y > 25; y--) {
            BlockPos blockPos = new BlockPos(netherTop.getX(), y, netherTop.getZ());
            if (BlockHelper.check5x5(Blocks.LAVA.getDefaultState(), world, blockPos)) {
                BlockHelper.fill5x5(Blocks.NETHER_BRICK.getDefaultState(), world, blockPos);
                BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, blockPos.up());
                BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, blockPos.up().up());
                BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, blockPos.up().up().up());

                player.setPositionAndUpdate(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
                if (world.getBlockState(blockPos.up().up().up().up()) != Blocks.AIR.getDefaultState()) {
                    BlockHelper.fill5x5(Blocks.NETHER_BRICK.getDefaultState(), world, blockPos.up().up().up().up());
                }
                if (BlockHelper.check6x6Wall(Blocks.LAVA.getDefaultState(), world, blockPos.up()) || BlockHelper.check6x6Wall(Blocks.LAVA.getDefaultState(), world, blockPos.up().up()) || BlockHelper.check6x6Wall(Blocks.LAVA.getDefaultState(), world, blockPos.up().up().up())) {
                    BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, blockPos.up());
                    BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, blockPos.up().up());
                    BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, blockPos.up().up().up());
                }
                world.setBlockState(player.getPosition().down(), ModBlocks.SPAWN_POINT.getDefaultState());
                CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
                worldData.hasSpawnPoint(true);
                break;
            }
        }
    }
    private static void findSpot(EntityPlayer player, World world) {
        BlockPos pos = player.getPosition();
        for (int y = 110; y > 35; y--) {
            BlockPos blockPos = new BlockPos(pos.getX(), y, pos.getZ());

            boolean checkForNetherrack = world.getBlockState(new BlockPos(pos.getX(), y - 1, pos.getZ())) == Blocks.NETHERRACK.getDefaultState();
            boolean checkForSoulSand = world.getBlockState(new BlockPos(pos.getX(), y - 1, pos.getZ())) == Blocks.SOUL_SAND.getDefaultState();
            boolean checkForNetherBrick = world.getBlockState(new BlockPos(pos.getX(), y - 1, pos.getZ())) == Blocks.NETHER_BRICK.getDefaultState();

            if (!BlockHelper.check5x5(Blocks.AIR.getDefaultState(), world, blockPos.down())) {
                if (y < 120 && world.getBlockState(blockPos.up()) == Blocks.AIR.getDefaultState() && world.getBlockState(blockPos.up().up()) == Blocks.AIR.getDefaultState()) {
                    player.setPositionAndUpdate(pos.getX() + 0.5, y, pos.getZ() + 0.5);
                    BlockHelper.fill5x5(Blocks.NETHER_BRICK.getDefaultState(), world, player.getPosition().down());

                    BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, player.getPosition());
                    BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, player.getPosition().up());
                    BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, player.getPosition().up().up());

                    BlockHelper.fill5x5(Blocks.NETHERRACK.getDefaultState(), world, player.getPosition().down().down());
                    BlockHelper.fill5x5(Blocks.NETHERRACK.getDefaultState(), world, player.getPosition().down().down().down());
                    world.setBlockState(player.getPosition().down(), ModBlocks.SPAWN_POINT.getDefaultState());
                    CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
                    worldData.hasSpawnPoint(true);
                    break;
                }
            }
        }
    }
    private static void lastResort(EntityPlayer player, World world) {
        Random random = new Random();
        int randomY = random.nextInt(80 + 1 - 40) + 50;

        player.setPositionAndUpdate(player.getPosition().getX() + 0.5, randomY, player.getPosition().getZ() + 0.5);

        BlockHelper.fill5x5(Blocks.NETHER_BRICK.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY - 1, player.getPosition().getZ()));
        BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY, player.getPosition().getZ()));
        BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY + 1, player.getPosition().getZ()));
        BlockHelper.fill5x5(Blocks.AIR.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY + 2, player.getPosition().getZ()));
        BlockHelper.fill5x5(Blocks.NETHER_BRICK.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY + 3, player.getPosition().getZ()));
        BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY, player.getPosition().getZ()));
        BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY + 1, player.getPosition().getZ()));
        BlockHelper.fill6x6Wall(Blocks.NETHER_BRICK.getDefaultState(), world, new BlockPos(player.getPosition().getX(), randomY + 2, player.getPosition().getZ()));
        world.setBlockState(player.getPosition().down(), ModBlocks.SPAWN_POINT.getDefaultState());
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        worldData.hasSpawnPoint(true);
    }

    @Override
    public void placeInPortal(@Nonnull Entity entity, float rotationYaw) {
        this.worldServer.getBlockState(new BlockPos((int) this.blockPos.getX(), (int) this.blockPos.getY(), (int) this.blockPos.getZ()));

        entity.setPosition(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
        entity.motionX = 0.0f;
        entity.motionY = 0.0f;
        entity.motionZ = 0.0f;
    }
}