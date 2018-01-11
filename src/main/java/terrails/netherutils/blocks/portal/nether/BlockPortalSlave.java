package terrails.netherutils.blocks.portal.nether;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import terrails.netherutils.blocks.portal.nether.render.TESRPortalSlave;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPortalSlave extends BlockBase {

    public BlockPortalSlave(String name) {
        super(Material.ROCK, name);
        setBlockUnbreakable();
        GameRegistry.registerTileEntity(TileEntityPortalSlave.class, name);
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityPortalSlave) {
            TileEntityPortalSlave te = (TileEntityPortalSlave) tileEntity;

            if (stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null) {
                    
                    if (compound.getInteger("yMaster") != 0)
                        te.setMasterPos(new BlockPos(compound.getInteger("xMaster"), compound.getInteger("yMaster"), compound.getInteger("zMaster")));
                }
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPortalSlave) {
            TileEntityPortalSlave te = (TileEntityPortalSlave) tile;

            ItemStack stack = new ItemStack(state.getBlock());
            if (!te.getMasterPos().equals(BlockPos.ORIGIN)) {
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }
            }

            NBTTagCompound compound = stack.getTagCompound();
            assert compound != null;

            if (!te.getMasterPos().equals(BlockPos.ORIGIN)) {
                compound.setInteger("xMaster", te.getMasterPos().getX());
                compound.setInteger("yMaster", te.getMasterPos().getY());
                compound.setInteger("zMaster", te.getMasterPos().getZ());
            }
            drops.add(stack);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPortalSlave();
    }
}
