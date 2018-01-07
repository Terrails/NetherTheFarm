package terrails.netherutils.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.Constants;
import terrails.netherutils.init.ModBlocks;

import java.util.Objects;

import static terrails.netherutils.blocks.BlockSoulSoil.MOISTURE;

public class BlockEvent {

    @SubscribeEvent
    public void onHoeUse(UseHoeEvent event) {
        if (!event.getCurrent().isEmpty()) {
            if (event.getWorld().getBlockState(event.getPos()).getBlock().equals(Blocks.SOUL_SAND)) {
                if (event.getWorld().isAirBlock(event.getPos().up())) {
                    boolean isWoodenHoe = event.getCurrent().getUnlocalizedName().toLowerCase().contains("wood");
                    if (!isWoodenHoe) {
                        setBlock(event.getEntityPlayer(), event.getWorld(), event.getPos(), ModBlocks.SOUL_SOIL.getDefaultState().withProperty(MOISTURE, false));
                        event.getCurrent().damageItem(1, event.getEntityPlayer());
                    } else if (!event.getWorld().isRemote) {
                        Constants.Log.playerMessage(event.getEntityPlayer(), "You cannot use a wooden hoe on soul sand!");
                    }
                }
            }
        }
    }

    private static void setBlock(EntityPlayer player, World world, BlockPos pos, IBlockState state) {
        world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        player.swingArm(getHand(player));

        if (!world.isRemote) {
            world.setBlockState(pos, state, 11);
            player.getActiveItemStack().damageItem(1, player);
            AxisAlignedBB axisalignedbb = Objects.requireNonNull(ModBlocks.SOUL_SOIL.getDefaultState().getCollisionBoundingBox(world, pos)).offset(pos);
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb)) {
                entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
            }
        }
    }
    private static EnumHand getHand(EntityPlayer player) {
        for (EnumHand hand : EnumHand.values()) {
            ItemStack currentStack = player.getHeldItem(hand);
            if (!currentStack.isEmpty()) {
                return hand;
            }
        }
        return EnumHand.MAIN_HAND;
    }
}
