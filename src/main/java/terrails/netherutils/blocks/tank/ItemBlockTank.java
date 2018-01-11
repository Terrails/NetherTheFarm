package terrails.netherutils.blocks.tank;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import terrails.netherutils.blocks.tank.TileEntityTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemBlockTank extends ItemBlock {

    public ItemBlockTank(Block block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
        if (fluidHandler == null)
            return;

        for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
            FluidStack fluidStack = properties.getContents();

            if (fluidStack == null) {
                return;
            }

            tooltip.add(TextFormatting.GREEN + "" + fluidStack.amount + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + properties.getCapacity() + TextFormatting.GRAY + " (" + TextFormatting.AQUA + fluidStack.getLocalizedName() + TextFormatting.GRAY + ")");
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, TileEntityTank.CAPACITY);
    }
}
