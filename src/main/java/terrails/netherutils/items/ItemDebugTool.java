package terrails.netherutils.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import terrails.netherutils.Constants;
import terrails.netherutils.blocks.portal.PortalRegistry;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalSlave;
import terrails.terracore.item.ItemBase;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDebugTool extends ItemBase {

    public ItemDebugTool(String name) {
        super(Constants.MOD_ID);
        setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
        setUnlocalizedName(name);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityPortalMaster) {
                TileEntityPortalMaster tile = (TileEntityPortalMaster) tileEntity;
                player.sendMessage(new TextComponentString("=====! Portal details !====="));
                player.sendMessage(new TextComponentString("Amount in world: " + PortalRegistry.LIST.size()));
                player.sendMessage(new TextComponentString("Slave Coords: " + "X: " + tile.getSlavePos().getX() + ", Y: " + tile.getSlavePos().getY() + ", Z: " + tile.getSlavePos().getZ()));
                player.sendMessage(new TextComponentString("Current Fuel: " + tile.getFuelAmount() + "/" + tile.getFuelCapacity()));
                player.sendMessage(new TextComponentString("Status: " + (tile.isActive() ? "Online" : "Offline")));
                player.sendMessage(new TextComponentString("Has All Items: " + tile.hasRequiredItems()));
                player.sendMessage(new TextComponentString("Has Pedestals: " + tile.hasRequiredBlocks()));
                player.sendMessage(new TextComponentString("!=======================!"));
            } else if (tileEntity instanceof TileEntityPortalSlave) {
                TileEntityPortalSlave tile = (TileEntityPortalSlave) tileEntity;
                player.sendMessage(new TextComponentString("=====! Portal details !====="));
                player.sendMessage(new TextComponentString("Amount of master portals in world: " + PortalRegistry.LIST.size()));
                player.sendMessage(new TextComponentString("Master Coords: " + "X: " + tile.getMasterPos().getX() + ", Y: " + tile.getMasterPos().getY() + ", Z: " + tile.getMasterPos().getZ()));
                player.sendMessage(new TextComponentString("Status: " + (tile.isActive() ? "Online" : "Offline")));
                player.sendMessage(new TextComponentString("!=======================!"));
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Debugging world when right clicking the portal!");
    }
}
