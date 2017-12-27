package terrails.netherutils.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemForbiddenFruit extends ItemFood {

    public ItemForbiddenFruit(String name) {
        super(7, 1.5F, false);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setRegistryName(name);
        setUnlocalizedName(name);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, @Nonnull EntityPlayer player) {
        doStuff(world, player);
        super.onFoodEaten(stack, world, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    private void doStuff(World world, EntityLivingBase entity) {
        if (world.isRemote && world.rand.nextInt(6) == 3) {
            entity.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 10F, world.rand.nextFloat() * 0.4F + 0.8F);
        }

        if (!world.isRemote && world.rand.nextInt(5) == 3) {
   //         world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, 5F, false);
        }

        Potion nausea = Potion.getPotionFromResourceLocation("nausea");
        Potion blindness = Potion.getPotionFromResourceLocation("blindness");
        Potion fire_resistance = Potion.getPotionFromResourceLocation("fire_resistance");
        Potion absorption = Potion.getPotionFromResourceLocation("absorption");
        Potion regeneration = Potion.getPotionFromResourceLocation("regeneration");

        if (!world.isRemote) {
            entity.addPotionEffect(new PotionEffect(Objects.requireNonNull(fire_resistance), 90 * 20));
            entity.addPotionEffect(new PotionEffect(Objects.requireNonNull(absorption), 60 * 20));
        }

        if (world.rand.nextInt(16) == 0 && !world.isRemote) {
            entity.addPotionEffect(new PotionEffect(Objects.requireNonNull(nausea), 5 * 20));
            entity.addPotionEffect(new PotionEffect(Objects.requireNonNull(blindness), 5 * 20));
            entity.addPotionEffect(new PotionEffect(Objects.requireNonNull(regeneration), 30 * 20, 1));
        }
    }
}
