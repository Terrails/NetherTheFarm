package terrails.netherutils.api.advancements;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import terrails.netherutils.Constants;
import terrails.netherutils.config.ConfigHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalItemTrigger implements ICriterionTrigger<PortalItemTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "portal_item");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener) {
        PortalItemTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners == null) {
            listeners = new PortalItemTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener) {
        PortalItemTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public PortalItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ItemStack stack = getStack(ConfigHandler.itemToLeaveNether);
        if (stack == ItemStack.EMPTY && !ConfigHandler.itemToLeaveNether.isEmpty())
            throw new NullPointerException("Unknown item '" + ConfigHandler.itemToLeaveNether + "'");

        boolean anyMeta = false;
        if (!ConfigHandler.itemToLeaveNether.contains("|") && stack != ItemStack.EMPTY)
            anyMeta = true;

        return new PortalItemTrigger.Instance(stack, anyMeta);
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

    public static class Instance extends AbstractCriterionInstance {
        private final ItemStack itemStack;
        private final boolean anyMeta;

        public Instance(@Nullable ItemStack itemStack, boolean anyMeta) {
            super(PortalItemTrigger.ID);
            this.itemStack = itemStack;
            this.anyMeta = anyMeta;
        }

        public boolean test(ItemStack itemStack) {
            if (this.itemStack != ItemStack.EMPTY) {
                if (this.itemStack != null && itemStack.getItem() != this.itemStack.getItem() && (anyMeta ? anyMeta : this.itemStack.getMetadata() == itemStack.getMetadata())) {
                    return false;
                } else return true;
            }
            return false;
        }
    }

    public void trigger(EntityPlayerMP player, ItemStack stack) {
        if (stack != ItemStack.EMPTY) {
            PortalItemTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

            if (listeners != null) {
                listeners.trigger(stack);
            }
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ItemStack stack) {
            List<Listener<Instance>> list = null;

            for (ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(stack)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<PortalItemTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}