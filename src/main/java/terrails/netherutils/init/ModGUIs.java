package terrails.netherutils.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import terrails.netherutils.gui.client.GuiPortal;
import terrails.netherutils.gui.inventory.PortalContainer;
import terrails.netherutils.blocks.portal.nether.TileEntityPortalMaster;

import javax.annotation.Nullable;

public class ModGUIs implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPortalMaster) {
            TileEntityPortalMaster tileEntityPortal = (TileEntityPortalMaster) tile;
            return new PortalContainer(player.inventory, tileEntityPortal);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPortalMaster) {
            TileEntityPortalMaster tileEntityPortal = (TileEntityPortalMaster) tile;
            return new GuiPortal(player.inventory, tileEntityPortal);
        }
        return null;
    }
}