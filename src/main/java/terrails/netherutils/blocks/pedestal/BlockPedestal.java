package terrails.netherutils.blocks.pedestal;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.terracore.block.BlockTileEntity;

import javax.annotation.Nullable;

public class BlockPedestal extends BlockTileEntity<TileEntityPedestal> {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 1, 0, 0.0625 * 1, 0.0625 * 15, 0.0625 * 16, 0.0625 * 15);

    public BlockPedestal(String name) {
        super(Material.ROCK, name);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setHardness(2F);
        setResistance(3F);
        setHarvestLevel("pickaxe", 1);
        GameRegistry.registerTileEntity(TileEntityPedestal.class, name);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntityPedestal te = getTileEntity(world, pos);
            if (te.getStack().isEmpty() && !player.getHeldItem(hand).isEmpty()) {
                ItemStack stack = player.getHeldItem(hand);
                ItemStack stackCopy = stack.copy();

                stackCopy.setCount(1);
                te.setStack(stackCopy);
                stack.shrink(1);
                player.openContainer.detectAndSendChanges();
            } else if (!te.getStack().isEmpty()) {
                ItemStack stack = te.getStack();
                if (player.inventory.addItemStackToInventory(stack)) {
                    te.setStack(ItemStack.EMPTY);
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityPedestal te = getTileEntity(world, pos);
        if (!te.getStack().isEmpty()) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getStack());
        }
    }

    // == Basic & Rendering == \\

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TESRPedestal());
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPedestal();
    }

    // == End == \\
}
