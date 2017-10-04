package terrails.netherthefarm.items.block;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import terrails.netherthefarm.blocks.BlockTank;
import terrails.netherthefarm.tile.TileEntityTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBlockTank extends ItemBlock {

    public ItemBlockTank(Block block) {
        super(block);
        setHasSubtypes(true);
    //    setMaxStackSize(1);
        setMaxStackSize(64);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final NBTTagCompound tankData = stack.getTagCompound();
        if (tankData != null) {
            String name = tankData.getString(BlockTank.NBTData.LOCALIZED_NAME.getName());
            int amount = tankData.getInteger(BlockTank.NBTData.AMOUNT.getName());
            int capacity = tankData.getInteger(BlockTank.NBTData.CAPACITY.getName());
            if (amount != 0 && capacity >= amount)
                tooltip.add(TextFormatting.GREEN + "" + amount + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + capacity + TextFormatting.GRAY + " (" + TextFormatting.AQUA + name + TextFormatting.GRAY + ")");
         //   tooltip.add(amount + "/" + capacity + " (" + name + ")");
        }
    }
}