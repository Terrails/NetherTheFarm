package terrails.netherutils.entity.capabilities.portalitem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;
import terrails.netherutils.api.capabilities.IPortalItem;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityPortalItem {

    @CapabilityInject(IPortalItem.class)
    public static final Capability<IPortalItem> PORTAL_ITEM_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPortalItem.class, new Capability.IStorage<IPortalItem>() {

            @Override
            public NBTBase writeNBT(Capability<IPortalItem> capability, IPortalItem instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean("hasCrafted", instance.hasCrafted());
                return compound;
            }
            @Override
            public void readNBT(Capability<IPortalItem> capability, IPortalItem instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.hasCrafted(compound.getBoolean("hasCrafted"));
            }
        }, PortalItem::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(Constants.MOD_ID, "PortalItem"), new CapabilitySerializable<>(CapabilityPortalItem.PORTAL_ITEM_CAPABILITY));
            }
        }

        @SubscribeEvent
        public void playerClone(PlayerEvent.Clone event) {
            EntityPlayer player = event.getEntityPlayer();
            IPortalItem portalItem = player.getCapability(CapabilityPortalItem.PORTAL_ITEM_CAPABILITY, null);
            IPortalItem oldPortalItem = event.getOriginal().getCapability(CapabilityPortalItem.PORTAL_ITEM_CAPABILITY, null);
            if (portalItem != null && oldPortalItem != null) {
                portalItem.hasCrafted(oldPortalItem.hasCrafted());
            }
        }
    }

    static {
        PORTAL_ITEM_CAPABILITY = null;
    }
}
