package terrails.netherutils.entity.capabilities.firstspawn;

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
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.capabilities.IFirstSpawn;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityFirstSpawn {

    @CapabilityInject(IFirstSpawn.class)
    public static final Capability<IFirstSpawn> FIRST_SPAWN_CAPABILITY;


    public static void register() {
        CapabilityManager.INSTANCE.register(IFirstSpawn.class, new Capability.IStorage<IFirstSpawn>() {

            @Override
            public NBTBase writeNBT(Capability<IFirstSpawn> capability, IFirstSpawn instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean("StartingEffects", instance.hasStartingEffects());
                compound.setBoolean("StartingItems", instance.hasStartingItems());
                compound.setBoolean("IsNew", instance.isNew());
                return compound;
            }
            @Override
            public void readNBT(Capability<IFirstSpawn> capability, IFirstSpawn instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.hasStartingEffects(compound.getBoolean("StartingEffects"));
                instance.hasStartingItems(compound.getBoolean("StartingItems"));
                instance.isNew(compound.getBoolean("IsNew"));
            }
        }, FirstSpawn::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(NetherUtils.MOD_ID, "FirstSpawn"), new CapabilitySerializable<>(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY));
            }
        }

        @SubscribeEvent
        public void playerClone(PlayerEvent.Clone event) {
            EntityPlayer player = event.getEntityPlayer();
            IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            IFirstSpawn oldFirstSpawn = event.getOriginal().getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            if (firstSpawn != null && oldFirstSpawn != null) {
                firstSpawn.hasStartingEffects(oldFirstSpawn.hasStartingEffects());
                firstSpawn.hasStartingItems(oldFirstSpawn.hasStartingItems());
                firstSpawn.isNew(oldFirstSpawn.isNew());
            }
        }
    }

    static {
        FIRST_SPAWN_CAPABILITY = null;
    }
}
