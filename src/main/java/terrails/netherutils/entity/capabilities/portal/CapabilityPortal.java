package terrails.netherutils.entity.capabilities.portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;
import terrails.netherutils.api.capabilities.IPortal;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityPortal {

    @CapabilityInject(IPortal.class)
    public static final Capability<IPortal> PORTAL_ITEM_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPortal.class, new Capability.IStorage<IPortal>() {

            @Override
            public NBTBase writeNBT(Capability<IPortal> capability, IPortal instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("LastPortalX", instance.lastMasterPos().getX());
                compound.setInteger("LastPortalY", instance.lastMasterPos().getY());
                compound.setInteger("LastPortalZ", instance.lastMasterPos().getZ());
                return compound;
            }
            @Override
            public void readNBT(Capability<IPortal> capability, IPortal instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setLastMasterPos(new BlockPos(compound.getInteger("LastPortalX"), compound.getInteger("LastPortalY"), compound.getInteger("LastPortalZ")));
            }
        }, Portal::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(Constants.MOD_ID, "Portal"), new CapabilitySerializable<>(CapabilityPortal.PORTAL_ITEM_CAPABILITY));
            }
        }

        @SubscribeEvent
        public void playerClone(PlayerEvent.Clone event) {
            EntityPlayer player = event.getEntityPlayer();
            IPortal portalItem = player.getCapability(CapabilityPortal.PORTAL_ITEM_CAPABILITY, null);
            IPortal oldPortalItem = event.getOriginal().getCapability(CapabilityPortal.PORTAL_ITEM_CAPABILITY, null);
            if (portalItem != null && oldPortalItem != null) {
                portalItem.setLastMasterPos(oldPortalItem.lastMasterPos());
            }
        }
    }

    static {
        PORTAL_ITEM_CAPABILITY = null;
    }
}
