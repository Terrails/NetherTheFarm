package terrails.netherutils.event;

import com.google.common.base.CharMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import terrails.netherutils.Constants;
import terrails.netherutils.api.capabilities.IFirstSpawn;
import terrails.netherutils.api.capabilities.IObelisk;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.entity.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherutils.entity.capabilities.obelisk.CapabilityObelisk;
import terrails.netherutils.world.TeleporterNTF;
import terrails.netherutils.world.data.CustomWorldData;

import java.util.Objects;

public class SpawnEvent {

    @SubscribeEvent
    public void respawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        World world = player.getEntityWorld();
        IObelisk obelisk = player.getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
        IWorldData worldData = CustomWorldData.get(world);

        if (!world.isRemote && obelisk != null && worldData != null) {
            BlockPos respawnPoint = BlockPos.ORIGIN;

            if (ConfigHandler.pointRespawn && !worldData.getPointPos().equals(BlockPos.ORIGIN))
                respawnPoint = worldData.getPointPos();

            if (obelisk.hasObelisk() && obelisk.getObeliskDim() != Integer.MIN_VALUE)
                respawnPoint = obelisk.getObeliskPos();

            if (!respawnPoint.equals(BlockPos.ORIGIN)) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;

                if (respawnPoint.equals(obelisk.getObeliskPos()))
                    TeleporterNTF.teleport(playerMP, obelisk.getObeliskDim(), respawnPoint.up(), false);
                else if (respawnPoint.equals(worldData.getPointPos()))
                    TeleporterNTF.teleport(playerMP, -1, respawnPoint.up(), false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void firstSpawn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IWorldData worldData = CustomWorldData.get(player.getEntityWorld());
        IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);

        if (player.world.isRemote) return;
        if (worldData != null && firstSpawn != null) {
            if (ConfigHandler.pointRespawn) {
                if (!worldData.hasSpawnPoint()) {
                    TeleporterNTF.teleport(player, -1, player.getServerWorld().getSpawnPoint(), true);
                }
                if (firstSpawn.isNew()) {
                    TeleporterNTF.teleport(player, -1, worldData.getPointPos().up(), false);
                    firstSpawn.isNew(false);
                }
            }
            if (!firstSpawn.hasStartingItems() || !firstSpawn.hasStartingEffects()) {
                giveStartingItemsToPlayer(player);
            }
        }
    }

    private static void giveStartingItemsToPlayer(EntityPlayer player) {
        IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
        if (firstSpawn != null && !firstSpawn.hasStartingItems()) {
            for (String oneLine : ConfigHandler.startingItems) {
                String array = oneLine.toLowerCase();

                String itemName = array.substring(0, array.indexOf(" "));
                ItemStack stack = new ItemStack(Objects.requireNonNull(Item.getByNameOrId(itemName)), getItemQuantity(array));

                stack = getItemWithEnchant(array, stack);
                stack = getItemCustomName(oneLine, stack);
                if (Item.getByNameOrId(itemName) != null && stack != null) {
                    player.addItemStackToInventory(stack);
                    firstSpawn.hasStartingItems(true);
                } else {
                    Constants.Log.playerMessage(player, "Config Error with Starting Items: " + oneLine);
                }
            }
        }
        if (firstSpawn != null && !firstSpawn.hasStartingEffects()) {
            for (String oneLine : ConfigHandler.startingEffects) {
                String array = oneLine.toLowerCase();
                String effectName = array.substring(0, array.indexOf(" -"));
                if (Potion.getPotionFromResourceLocation(effectName) != null) {
                    PotionEffect potionEffect = new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation(effectName)), getEffectTime(array) * 20, getAmplifier(array), false, false);
                    player.addPotionEffect(potionEffect);
                    Objects.requireNonNull(player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null)).hasStartingEffects(true);
                } else {
                    Constants.Log.playerMessage(player, "Config Error with Starting Potion Effects: " + oneLine);
                }
            }
        }
    }

    private static ItemStack getItemWithEnchant(String oneLine, ItemStack stack) {
        if (oneLine.contains(" -enchantment:")) {
            String step1 = oneLine.substring(oneLine.indexOf("-enchantment:")).replace("-enchantment:", "");
            String step2 = step1.contains(" -") ? step1.replaceAll("([\\s]).*", "$1").replace(" ", "") : step1;

            if (step2.contains(";")) {
                String[] enchants = step2.split(";");
                for (String enchantment : enchants) {
                    String enchantLevelString = enchantment.contains(":") ? enchantment.replaceAll("^.*(:\\d+).*$", "$1").replace(":", "") : "0";
                    String enchantLevelDigit = CharMatcher.digit().retainFrom(enchantLevelString);
                    int enchantmentLevel = Integer.parseInt(enchantLevelDigit);

                    String enchant = enchantment.substring(0, enchantment.indexOf(":"));
                    stack.addEnchantment(Objects.requireNonNull(Enchantment.getEnchantmentByLocation(enchant)), enchantmentLevel);
                }
            } else {
                String enchant = step2.substring(0, step2.indexOf(":"));
                String enchantLevelString = step2.contains(":") ? step2.replaceAll("^.*(:\\d+).*$", "$1").replace(":", "") : "0";
                String enchantLevelFindDigit = CharMatcher.digit().retainFrom(enchantLevelString);
                int enchantLevel = Integer.parseInt(enchantLevelFindDigit);
                stack.addEnchantment(Objects.requireNonNull(Enchantment.getEnchantmentByLocation(enchant)), enchantLevel);
            }
        }
        return stack;
    }
    private static ItemStack getItemCustomName(String oneLine, ItemStack stack) {
        if (oneLine.contains(" -name:")) {
            String step1 = oneLine.substring(oneLine.indexOf("-name:")).replace("-name:", "");
            String step2 = step1.contains(" -") ? step1.replaceAll("([\\s]).*", "$1").replace(" ", "") : step1;
            String name = step2.contains("'") ? step2.replaceAll("'", "") : step2;
            stack.setStackDisplayName(name);
        }
        return stack;
    }
    private static int getItemQuantity(String oneLine) {
        if (oneLine.contains(" -quantity")) {
            String step1 = oneLine.substring(oneLine.indexOf("-quantity:")).replace("-quantity:", "");
            String step2 = step1.contains(" -") ? step1.replaceAll("([\\s]).*", "$1").replace(" ", "") : step1;

            String findDigit = CharMatcher.digit().retainFrom(step2);
            int itemQuantity = Integer.parseInt(findDigit);
            return itemQuantity;
        }
        return 1;
    }

    private static int getEffectTime(String oneLine) {
        if (oneLine.contains("-time:")) {
            String step1 = oneLine.substring(oneLine.indexOf("-time:")).replace("-time:", "");
            String step2 = step1.contains(" -") ? step1.replaceAll("([\\s]).*", "$1").replace(" ", "") : step1;

            String findDigit = CharMatcher.digit().retainFrom(step2);
            int effectTime = Integer.parseInt(findDigit);
            return effectTime;
        }
        return 0;
    }
    private static int getAmplifier(String oneLine) {
        if (oneLine.contains("-amplifier:")) {
            String step1 = oneLine.substring(oneLine.indexOf("-amplifier:")).replace("-amplifier:", "");
            String step2 = step1.contains(" -") ? step1.replaceAll("([\\s]).*", "$1").replace(" ", "") : step1;

            String findDigit = CharMatcher.digit().retainFrom(step2);
            int amplifierInt = Integer.parseInt(findDigit);
            return amplifierInt;
        }
        return 0;
    }
}
