package terrails.netherutils.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import terrails.netherutils.blocks.BlockTank;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.tileentity.TileEntityTank;
import terrails.terracore.capabilities.CapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemBlockTank extends ItemBlock {

    public ItemBlockTank(Block block) {
        super(block);
        if (block.getRegistryName() != null) {
            setRegistryName(block.getRegistryName());
        }
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
        if (fluidHandler != null) {
            for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
                FluidStack fluidStack = properties.getContents();

                if (fluidStack != null) {
                    tooltip.add(TextFormatting.GREEN + "" + fluidStack.amount + TextFormatting.GOLD + "/" + TextFormatting.GREEN + "" + properties.getCapacity() + TextFormatting.GRAY + " (" + TextFormatting.AQUA + fluidStack.getLocalizedName() + TextFormatting.GRAY + ")");
                }
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapabilitySerializable<>(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, new ItemFluidTank(stack, TileEntityTank.CAPACITY));
    }
    private class ItemFluidTank extends FluidTank implements IFluidHandlerItem {
        private final ItemStack container;

        private ItemFluidTank(ItemStack container, int capacity) {
            super(capacity);
            this.container = container;
        }

        @Nonnull
        @Override
        public ItemStack getContainer() {
            return container;
        }

        @Override
        public boolean equals(@Nullable final Object obj) {
            return this == obj || obj != null && getClass() == obj.getClass() && Objects.equals(getFluid(), ((FluidTank) obj).getFluid());
        }

        @Override
        public int hashCode() {
            return fluid != null ? fluid.hashCode() : 0;
        }
    }
}
