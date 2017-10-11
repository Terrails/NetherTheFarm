package terrails.netherthefarm.event;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.world.*;
import terrails.netherthefarm.world.data.CustomWorldData;

@Mod.EventBusSubscriber
public class NetherEvent {

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void deathEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player.getEntityWorld().getWorldType().getName().equals(Constants.NETHER_SURVIVAL.getName()) || event.player.getEntityWorld().getWorldType().getName().equals("BIOMESOP")) {
            if (ConfigHandler.obelisk) {
                EntityPlayer player = event.player;
                EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
                World world = playerMP.getEntityWorld();
                CustomWorldData worldData = CustomWorldData.get(playerMP.getEntityWorld());
                IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);

                if (!world.isRemote && firstSpawn != null) {
                    BlockPos obeliskPos = new BlockPos(firstSpawn.posX(), firstSpawn.posY(), firstSpawn.posZ());
                    BlockPos spawnPointPos = new BlockPos(worldData.posX(), worldData.posY() + 1, worldData.posZ());
                    BlockPos nullPos = new BlockPos(0, 0, 0);

                    int dimension = Integer.MIN_VALUE;
                    BlockPos respawnPoint;
                    respawnPoint = spawnPointPos;
                    if (player.getBedLocation() != player.world.provider.getSpawnCoordinate())
                        respawnPoint = player.getBedLocation(firstSpawn.oldPlayerDimension());
                    else if (obeliskPos != nullPos && firstSpawn.hasObelisk() == 1 && firstSpawn.obeliskDim() != Integer.MIN_VALUE)
                        respawnPoint = obeliskPos;
                    else {
                        for (int dim = 100; dim >= -100; dim--) {
                            if (player.getBedLocation(dim) != player.world.provider.getSpawnCoordinate()) {
                                respawnPoint = player.getBedLocation(dim);
                                dimension = dim;
                            }
                        }
                    }

                    if (respawnPoint != nullPos) {
                        if (respawnPoint == player.getBedLocation(firstSpawn.oldPlayerDimension()))
                            TeleporterNTF.teleportToDimension(playerMP, firstSpawn.oldPlayerDimension(), BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                        else if (respawnPoint == obeliskPos)
                            TeleporterNTF.teleportToDimension(playerMP, firstSpawn.obeliskDim(), respawnPoint.up());
                        else if (respawnPoint == spawnPointPos && ConfigHandler.obelisk)
                            TeleporterNTF.teleportToDimension(playerMP, worldData.spawnPointDim(), respawnPoint);
                        else if (respawnPoint == player.getBedLocation(dimension))
                            TeleporterNTF.teleportToDimension(playerMP, dimension, BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                    }

                }
            }
        }
        else {
            EntityPlayer player = event.player;
            EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
            World world = playerMP.getEntityWorld();
            IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);

            if (!world.isRemote) {
                BlockPos obeliskPos = new BlockPos(firstSpawn.posX(), firstSpawn.posY(), firstSpawn.posZ());
                BlockPos nullPos = new BlockPos(0, 0, 0);

                int dimension = Integer.MIN_VALUE;
                BlockPos respawnPoint = nullPos;
                if (player.getBedLocation() != player.world.provider.getSpawnCoordinate())
                    respawnPoint = player.getBedLocation(firstSpawn.oldPlayerDimension());
                else if (obeliskPos != nullPos && firstSpawn.hasObelisk() == 1 && firstSpawn.obeliskDim() != Integer.MIN_VALUE)
                    respawnPoint = obeliskPos;
                else {
                    for (int dim = 100; dim >= -100; dim--) {
                        if (player.getBedLocation(dim) != player.world.provider.getSpawnCoordinate()) {
                            respawnPoint = player.getBedLocation(dim);
                            dimension = dim;
                        }
                    }
                }


                if (respawnPoint != nullPos) {
                    if (respawnPoint == player.getBedLocation(firstSpawn.oldPlayerDimension()))
                        TeleporterNTF.teleportToDimension(playerMP, firstSpawn.oldPlayerDimension(), BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                    else if (respawnPoint == obeliskPos)
                        TeleporterNTF.teleportToDimension(playerMP, firstSpawn.obeliskDim(), respawnPoint.up());
                    else if (respawnPoint == player.getBedLocation(dimension))
                        TeleporterNTF.teleportToDimension(playerMP, dimension, BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void firstSpawn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());

        if (event.player.getEntityWorld().getWorldType().getName().equals(Constants.NETHER_SURVIVAL.getName()) || event.player.getEntityWorld().getWorldType().getName().equals("BIOMESOP")) {
            if (player.world.isRemote) return;
            if (!worldData.hasSpawnPoint() && ConfigHandler.obelisk) teleportPlayer(player);
        }
    }
    private static void teleportPlayer(EntityPlayerMP player) {
        BlockPos spawnPoint = player.getServerWorld().getSpawnPoint();
        TeleporterNTF.teleportToDimension(player, -1, spawnPoint);
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        worldData.hasSpawnPoint(true);
    }
}
