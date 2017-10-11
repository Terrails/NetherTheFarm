package terrails.netherthefarm.event;

import com.google.common.base.CharMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.world.data.CustomWorldData;

@Mod.EventBusSubscriber
public class FirstSpawnEvent {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void firstSpawn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        EntityPlayerMP playermp = (EntityPlayerMP) event.player;
        CustomWorldData worldData = CustomWorldData.get(playermp.getEntityWorld());
        IFirstSpawn firstSpawn = playermp.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);

        if (firstSpawn.hasStartingFeatures() == 0) giveStartingItemsToPlayer(player);
    }
    private static void giveStartingItemsToPlayer(EntityPlayer player) {
        for (String oneLine : ConfigHandler.startingItems) {
            String array = oneLine.toLowerCase();

            String itemName = array.substring(0, array.indexOf(" "));
            ItemStack stack = new ItemStack(Item.getByNameOrId(itemName), getItemQuantity(array));

            stack = getItemWithEnchant(array, stack);
            stack = getItemCustomName(oneLine, stack);
            if (Item.getByNameOrId(itemName) != null && stack != null)
                player.addItemStackToInventory(stack);
            else {
                Constants.playerMessage(player, "Config Error with Starting Items: " + oneLine);
            }
        }
        for (String oneLine : ConfigHandler.startingPotions) {
            String array = oneLine.toLowerCase();
            String effectName = array.substring(0, array.indexOf(" -"));
            if (Potion.getPotionFromResourceLocation(effectName) != null) {
                PotionEffect potionEffect = new PotionEffect(Potion.getPotionFromResourceLocation(effectName), getEffectTime(array) * 20, getAmplifier(array), false, false);
                player.addPotionEffect(potionEffect);
            } else {
                Constants.playerMessage(player, "Config Error with Starting Potion Effects: " + oneLine);
            }
        }
        player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null).hasStartingFeatures(1);
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
                    stack.addEnchantment(Enchantment.getEnchantmentByLocation(enchant), enchantmentLevel);
                }
            } else {
                String enchant = step2.substring(0, step2.indexOf(":"));
                String enchantLevelString = step2.contains(":") ? step2.replaceAll("^.*(:\\d+).*$", "$1").replace(":", "") : "0";
                String enchantLevelFindDigit = CharMatcher.digit().retainFrom(enchantLevelString);
                int enchantLevel = Integer.parseInt(enchantLevelFindDigit);
                stack.addEnchantment(Enchantment.getEnchantmentByLocation(enchant), enchantLevel);
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
