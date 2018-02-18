package terrails.netherutils.blocks.portal.end;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.blocks.portal.end.BlockPortal;
import terrails.netherutils.blocks.portal.end.TileEntityPortalSlave;
import terrails.netherutils.blocks.portal.end.render.TESRPortalSlave;
import terrails.terracore.block.BlockBase;
import terrails.terracore.block.BlockTileEntity;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPortalSlave extends BlockTileEntity<TileEntityPortalSlave> {

    public BlockPortalSlave(String name) {
        super(Material.ROCK, name);
        setBlockUnbreakable();
        GameRegistry.registerTileEntity(TileEntityPortalSlave.class, "end_portal_slave");
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalSlave.class, new TESRPortalSlave());
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockPortal.BOUNDING_BOX;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPortalSlave();
    }
}
