package terrails.netherthefarm.capabilities.firstspawn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Capability for {@link IFirstSpawn}.
 *
 * @author Terrails
 */

public class CapabilityFirstSpawn implements ICapabilitySerializable<NBTBase> {

    @CapabilityInject(IFirstSpawn.class)
    public static final Capability<IFirstSpawn> FIRST_SPAWN_CAPABILITY = null;
    public static final ResourceLocation CAPABILITY = new ResourceLocation(Constants.MOD_ID, "FirstSpawn");

    private IFirstSpawn instance = FIRST_SPAWN_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == FIRST_SPAWN_CAPABILITY;
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == FIRST_SPAWN_CAPABILITY ? FIRST_SPAWN_CAPABILITY.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return FIRST_SPAWN_CAPABILITY.writeNBT(this.instance, null);
    }
    @Override
    public void deserializeNBT(NBTBase nbt) {
        FIRST_SPAWN_CAPABILITY.readNBT(this.instance, null, nbt);
    }
    public static void register() {
        CapabilityManager.INSTANCE.register(IFirstSpawn.class, new Capability.IStorage<IFirstSpawn>() {

            @Override
            public NBTBase writeNBT(Capability<IFirstSpawn> capability, IFirstSpawn instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("hasObelisk", instance.hasObelisk());
                compound.setInteger("obeliskPosX", instance.posX());
                compound.setInteger("obeliskPosY", instance.posY());
                compound.setInteger("obeliskPosZ", instance.posZ());
                compound.setInteger("obeliskDim", instance.obeliskDim());
                compound.setInteger("startingFeatures", instance.hasStartingFeatures());
                compound.setInteger("playerDimBeforeDeath", instance.oldPlayerDimension());
                return compound;
            }
            @Override
            public void readNBT(Capability<IFirstSpawn> capability, IFirstSpawn instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound)nbt;
                instance.hasObelisk(compound.getInteger("hasObelisk"));
                instance.setPosX(compound.getInteger("obeliskPosX"));
                instance.setPosY(compound.getInteger("obeliskPosY"));
                instance.setPosZ(compound.getInteger("obeliskPosZ"));
                instance.setObeliskDim(compound.getInteger("obeliskDim"));
                instance.hasStartingFeatures(compound.getInteger("startingFeatures"));
                instance.oldPlayerDimension(compound.getInteger("playerDimBeforeDeath"));
            }
        }, () -> new FirstSpawn());
    }

    @Mod.EventBusSubscriber
    public static class Handler {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(CAPABILITY, new CapabilityFirstSpawn());
            }
        }

        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            EntityPlayer player = event.getEntityPlayer();
            IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            IFirstSpawn oldFirstSpawn = event.getOriginal().getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            if (firstSpawn != null && oldFirstSpawn != null) {
                firstSpawn.hasObelisk(oldFirstSpawn.hasObelisk());
                firstSpawn.setPosX(oldFirstSpawn.posX());
                firstSpawn.setPosY(oldFirstSpawn.posY());
                firstSpawn.setPosZ(oldFirstSpawn.posZ());
                firstSpawn.setObeliskDim(oldFirstSpawn.obeliskDim());
                firstSpawn.hasStartingFeatures(oldFirstSpawn.hasStartingFeatures());
                firstSpawn.oldPlayerDimension(event.getOriginal().world.provider.getDimension());
            }
        }
    }
}
