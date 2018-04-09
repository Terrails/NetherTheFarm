package terrails.netherutils.entity.capabilities.obelisk;

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
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.capabilities.IObelisk;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityObelisk {

    @CapabilityInject(IObelisk.class)
    public static final Capability<IObelisk> OBELISK_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IObelisk.class, new Capability.IStorage<IObelisk>() {

            @Override
            public NBTBase writeNBT(Capability<IObelisk> capability, IObelisk instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("PosX", instance.getObeliskPos().getX());
                compound.setInteger("PosY", instance.getObeliskPos().getY());
                compound.setInteger("PosZ", instance.getObeliskPos().getZ());
                compound.setInteger("Dimension", instance.getObeliskDim());
                return compound;
            }
            @Override
            public void readNBT(Capability<IObelisk> capability, IObelisk instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setObeliskDim(compound.getInteger("Dimension"));
                BlockPos pos = new BlockPos(compound.getInteger("PosX"), compound.getInteger("PosY"), compound.getInteger("PosZ"));
                instance.setObeliskPos(pos);
            }
        }, Obelisk::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(NetherUtils.MOD_ID, "Obelisk"), new CapabilitySerializable<>(CapabilityObelisk.OBELISK_CAPABILITY));
            }
        }

        @SubscribeEvent
        public void playerClone(PlayerEvent.Clone event) {
            EntityPlayer player = event.getEntityPlayer();
            IObelisk obelisk = player.getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
            IObelisk oldObelisk = event.getOriginal().getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
            if (obelisk != null && oldObelisk != null) {
                obelisk.setObeliskPos(oldObelisk.getObeliskPos());
                obelisk.setObeliskDim(oldObelisk.getObeliskDim());
            }
        }
    }

    static {
        OBELISK_CAPABILITY = null;
    }
}
