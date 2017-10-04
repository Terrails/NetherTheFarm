package terrails.netherthefarm.event;

import com.google.common.base.CharMatcher;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import sun.nio.ch.Net;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;
import terrails.netherthefarm.api.world.data.IWorldData;
import terrails.netherthefarm.blocks.ModBlocks;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.world.*;
import terrails.netherthefarm.world.data.CustomWorldData;

import java.util.ArrayList;
import java.util.List;

public class NetherEvent {

    @SubscribeEvent
    public void deathEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player.getEntityWorld().getWorldType().getName().equals("NetherSurvival") || event.player.getEntityWorld().getWorldType().getName().equals("BIOMESOP")) {
            if (ConfigHandler.obelisk) {
                EntityPlayer player = event.player;
                EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
                World world = playerMP.getEntityWorld();
                CustomWorldData worldData = CustomWorldData.get(playerMP.getEntityWorld());
                IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);

                if (!world.isRemote) {
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
                            CustomTeleporter.teleportToDimension(playerMP, firstSpawn.oldPlayerDimension(), BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                        else if (respawnPoint == obeliskPos)
                            CustomTeleporter.teleportToDimension(playerMP, firstSpawn.obeliskDim(), respawnPoint.up());
                        else if (respawnPoint == spawnPointPos && ConfigHandler.obelisk)
                            CustomTeleporter.teleportToDimension(playerMP, worldData.spawnPointDim(), respawnPoint);
                        else if (respawnPoint == player.getBedLocation(dimension))
                            CustomTeleporter.teleportToDimension(playerMP, dimension, BlockBed.getSafeExitLocation(world, respawnPoint, 0));
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
                        CustomTeleporter.teleportToDimension(playerMP, firstSpawn.oldPlayerDimension(), BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                    else if (respawnPoint == obeliskPos)
                        CustomTeleporter.teleportToDimension(playerMP, firstSpawn.obeliskDim(), respawnPoint.up());
                    else if (respawnPoint == player.getBedLocation(dimension))
                        CustomTeleporter.teleportToDimension(playerMP, dimension, BlockBed.getSafeExitLocation(world, respawnPoint, 0));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void firstSpawn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        EntityPlayerMP playermp = (EntityPlayerMP) event.player;

        boolean isRemote = ((EntityPlayerMP) event.player).world.isRemote;
        CustomWorldData worldData = CustomWorldData.get(playermp.getEntityWorld());

        if (event.player.getEntityWorld().getWorldType().getName().equals("NetherSurvival") || event.player.getEntityWorld().getWorldType().getName().equals("BIOMESOP")) {
            if (isRemote) return;
            if (!worldData.hasSpawnPoint() && ConfigHandler.obelisk) teleportPlayer(playermp);
        }
    }
    private void teleportPlayer(EntityPlayerMP player) {
        BlockPos spawnPoint = player.getServerWorld().getSpawnPoint();
        CustomTeleporter.teleportToDimension(player, -1, spawnPoint);
        CustomWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        worldData.hasSpawnPoint(true);
    }
}
