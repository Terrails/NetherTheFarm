package terrails.netherutils.event;

import com.google.common.base.CharMatcher;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.Constants;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.init.ModAdvancements;
import terrails.netherutils.init.ModBlocks;
import terrails.terracore.helper.BlockHelper;

public class EntityEvent {

    @SubscribeEvent
    public void inventoryChanged(TickEvent.PlayerTickEvent event) {
        if (!ConfigHandler.itemToLeaveNether.isEmpty() && ConfigHandler.pointRespawn) {
            if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
                for (ItemStack stack : event.player.inventory.mainInventory) {
                    if (stack.getItem() == getStack(ConfigHandler.itemToLeaveNether).getItem() && (!ConfigHandler.itemToLeaveNether.contains("|") || stack.getMetadata() == getStack(ConfigHandler.itemToLeaveNether).getMetadata())) {
                        ModAdvancements.PORTAL_ITEM_TRIGGER.trigger((EntityPlayerMP) event.player, stack);
                    }
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

        if (ConfigHandler.pointRespawn && event.getEntity().dimension == -1) {

            if (!ConfigHandler.vanillaPortal) {
                boolean anyPortal = BlockHelper.checkAny(1, player.getPosition(), player.getEntityWorld(), Blocks.PORTAL.getDefaultState(), false, true);
                boolean anyObsidian = BlockHelper.checkAny(1, player.getPosition(), player.getEntityWorld(), Blocks.OBSIDIAN.getDefaultState(), false, true);
                if (anyObsidian || anyPortal) {
                    cancel = true;
                }
            }
            
            if (player.getServer() != null && !ConfigHandler.itemToLeaveNether.isEmpty() && !cancel) {
                Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(Constants.MOD_ID, "portal_item"));
                if (advancement != null && !player.getAdvancements().getProgress(advancement).isDone()) {
                    cancel = true;
                }

            }
        }

        if (cancel) {
            String message = event.getEntity().dimension == -1 ? "leave the nether!" : "";
            player.sendMessage(new TextComponentString("You're not allowed to " + message));
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
