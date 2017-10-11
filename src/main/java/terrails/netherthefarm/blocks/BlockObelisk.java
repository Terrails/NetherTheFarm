package terrails.netherthefarm.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.NetherTheFarm;
import terrails.netherthefarm.api.capabilities.IFirstSpawn;
import terrails.netherthefarm.capabilities.firstspawn.CapabilityFirstSpawn;
import terrails.netherthefarm.render.TileEntityObeliskRenderer;
import terrails.netherthefarm.tile.TileEntityObelisk;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockObelisk extends BlockBase implements ITileEntityProvider {

    public static final IProperty<Boolean> IS_HANDLES = PropertyBool.create("is_handles");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 0, 0, 0.0625 * 0, 0.0625 * 16, 0.0625 * 2, 0.0625 * 16);

    public BlockObelisk(String name) {
        super(Material.ROCK, name);
        setHardness(4.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Constants.NTF_TAB);
        setDefaultState(blockState.getBaseState().withProperty(IS_HANDLES, true));
        GameRegistry.registerTileEntity(TileEntityObelisk.class, "obelisk");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking() && hand == EnumHand.MAIN_HAND) {
            TileEntityObelisk te = getTE(worldIn, pos);
            if (!te.hasTESR())
                te.setHasTESR(true);
            else if (te.hasTESR())
                te.setHasTESR(false);
        } else if (!playerIn.isSneaking() && !worldIn.isRemote) {
            IFirstSpawn iFirstSpawn = playerIn.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            boolean sendMessageOrNot = false;
            if (iFirstSpawn.posX() != pos.getX()) {
                iFirstSpawn.setPosX(pos.getX());
                sendMessageOrNot = true;
            }
            if (iFirstSpawn.posY() != pos.getY()) {
                iFirstSpawn.setPosY(pos.getY());
                sendMessageOrNot = true;
            }
            if (iFirstSpawn.posZ() != pos.getZ()) {
                iFirstSpawn.setPosZ(pos.getZ());
                sendMessageOrNot = true;
            }
            if (iFirstSpawn.obeliskDim() != playerIn.world.provider.getDimension()) {
                iFirstSpawn.setObeliskDim(playerIn.world.provider.getDimension());
                sendMessageOrNot = true;
            }
            if (sendMessageOrNot) {
                playerIn.sendMessage(new TextComponentString("[" + TextFormatting.RED + "NetherTheFarm" + TextFormatting.RESET + "]" + " New obelisk location set to: " + "X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ", in dimension: " + playerIn.world.provider.getDimension()));
            } else {
                playerIn.sendMessage(new TextComponentString("Obelisk already defined as a spawn point!"));
            }
        }
        return true;
    }


    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityObelisk.class, new TileEntityObeliskRenderer());
    }
    private TileEntityObelisk getTE(World world, BlockPos pos) {
        return (TileEntityObelisk) world.getTileEntity(pos);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(IS_HANDLES, false);
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_HANDLES);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) placer;
            IFirstSpawn firstSpawn = playerMP.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            firstSpawn.setObeliskDim(playerMP.world.provider.getDimension());
            firstSpawn.setPosX(pos.getX());
            firstSpawn.setPosY(pos.getY());
            firstSpawn.setPosZ(pos.getZ());
            firstSpawn.hasObelisk(1);
        }
        getTE(worldIn, pos).setHasTESR(true);
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!worldIn.isRemote) {
            IFirstSpawn firstSpawn = player.getCapability(CapabilityFirstSpawn.FIRST_SPAWN_CAPABILITY, null);
            firstSpawn.setObeliskDim(Integer.MIN_VALUE);
            firstSpawn.setPosX(0);
            firstSpawn.setPosY(0);
            firstSpawn.setPosZ(0);
            firstSpawn.hasObelisk(0);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityObelisk();
    }
}
