package terrails.netherutils.event;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.Constants;
import terrails.netherutils.api.capabilities.IDeathZone;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.entity.capabilities.deathzone.CapabilityDeathZone;
import terrails.netherutils.init.ModAdvancements;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketTitle;
import terrails.netherutils.world.TeleporterNTF;

import java.util.List;

public class EntityEvent {

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
            EntityPlayer player = event.player;

            if (!ConfigHandler.itemToLeaveNether.isEmpty() && ConfigHandler.pointRespawn) {
                for (ItemStack stack : player.inventory.mainInventory) {
                    if (stack.getItem() == getStack(ConfigHandler.itemToLeaveNether).getItem() && (!ConfigHandler.itemToLeaveNether.contains("|") || stack.getMetadata() == getStack(ConfigHandler.itemToLeaveNether).getMetadata())) {
                        ModAdvancements.PORTAL_ITEM_TRIGGER.trigger((EntityPlayerMP) player, stack);
                    }

                }
            }

            if (player.isEntityAlive() && player.world.provider.getDimensionType().getName().equalsIgnoreCase(DimensionType.NETHER.getName()) && ConfigHandler.maxYNether != 0) {
                //Constants.Log.getLogger().info("Side: " + event.side.name() + ", Phase: " + event.phase.name() + ", Time: " + event.player.getEntityWorld().getWorldTime());
                IDeathZone deathZone = player.getCapability(CapabilityDeathZone.DEATH_ZONE_CAPABILITY, null);
                if (deathZone == null)
                    return;

                if (player.getPosition().getY() > ConfigHandler.maxYNether) {
                    deathZone.tickCounter(deathZone.tickCounter() + 1);
                    if (deathZone.tickCounter() % 20 == 0 && (deathZone.getDeathCounter() > -1)) {
                        if (deathZone.getDeathCounter() > 0 && !ConfigHandler.showWarningOnSec.isEmpty()) {

                            if (ConfigHandler.showWarningOnSec.equalsIgnoreCase("0")) {
                                ModFeatures.Network.WRAPPER.sendTo(new CPacketTitle(deathZone.getDeathCounter()), (EntityPlayerMP) event.player);
                            } else {
                                String[] strings = ConfigHandler.showWarningOnSec.split(",");
                                List<Integer> times = Lists.newArrayList();
                                for (String string : strings) {
                                    int i = Integer.parseInt(string);
                                    times.add(i);
                                }
                                for (int time : times) {
                                    if (deathZone.getDeathCounter() == time) {
                                        ModFeatures.Network.WRAPPER.sendTo(new CPacketTitle(deathZone.getDeathCounter()), (EntityPlayerMP) event.player);
                                        break;
                                    }
                                }
                            }
                        }
                        deathZone.setDeathCounter(deathZone.getDeathCounter() - 1);
                    }

                    if (deathZone.getDeathCounter() == -2) {
                        deathZone.setDeathCounter(ConfigHandler.deathZoneTimer);
                    } else if (deathZone.getDeathCounter() == -1) {
                        deathZone.setDeathCounter(-2);
                        ModFeatures.Network.WRAPPER.sendTo(new CPacketTitle(-1), (EntityPlayerMP) event.player);
                        player.onKillCommand();
                        player.setDead();
                    }
                } else if (deathZone.getDeathCounter() != -2 || deathZone.tickCounter() != 0) {
                    deathZone.setDeathCounter(-2);
                    deathZone.tickCounter(0);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerChangeDim(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer))
            return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        boolean cancel = false;
        boolean isAdvancement = false;
        if (ConfigHandler.pointRespawn) {

            if (!ConfigHandler.vanillaPortal) {
                boolean anyPortal = TeleporterNTF.checkAny(2, player.getPosition(), player.getEntityWorld(), Blocks.PORTAL.getDefaultState(), false, true);
                if (anyPortal) {
                    cancel = true;
                }
            }

            String dimName = event.getEntity().getEntityWorld().provider.getDimensionType().getName();
            if (dimName.equalsIgnoreCase(DimensionType.NETHER.getName()) && player.getServer() != null && !ConfigHandler.itemToLeaveNether.isEmpty() && !cancel) {
                Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(Constants.MOD_ID, "portal_item"));
                if (advancement != null && !player.getAdvancements().getProgress(advancement).isDone()) {
                    cancel = true;
                    isAdvancement = true;
                }
            }

            if (dimName.equalsIgnoreCase(DimensionType.NETHER.getName()) && cancel && player.getServer() != null && player.canUseCommand(2, "")) {
                cancel = false;
            }
        }

        if (cancel) {
            String message = isAdvancement ? "You can't leave the nether without the required item!" : "You're not allowed to do this!";
            player.sendMessage(new TextComponentString(message));
            event.setCanceled(true);
        }
    }

    private static ItemStack getStack(String string) {
        int meta = 0;
        if (string.contains("|")) {
            string = string.substring(string.indexOf("|") + 1);
            meta = Integer.parseInt(CharMatcher.digit().retainFrom(string));
        }
        Item item = Item.getByNameOrId(string.contains("|") ? string.substring(0, string.indexOf("|")) : string);
        if (item != null) {
            return new ItemStack(item, 1, meta);
        } else return ItemStack.EMPTY;
    }
}
