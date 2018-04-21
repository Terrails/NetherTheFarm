package terrails.netherutils.blocks.obelisk;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.capabilities.IObelisk;
import terrails.netherutils.entity.capabilities.obelisk.CapabilityObelisk;
import terrails.terracore.block.BlockBase;
import terrails.terracore.registry.client.ICustomModel;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockObelisk extends BlockBase implements ICustomModel {

    public static final PropertyBool HAS_TESR = PropertyBool.create("has_tesr");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0625 * 0, 0, 0.0625 * 0, 0.0625 * 16, 0.0625 * 2, 0.0625 * 16);

    public BlockObelisk(String name) {
        super(Material.ROCK);
        this.setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        this.setUnlocalizedName(NetherUtils.MOD_ID + "." + name);
        this.setHardness(4.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(NetherUtils.TAB_NETHER_UTILS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_TESR, false));
        GameRegistry.registerTileEntity(TileEntityObelisk.class, "obelisk");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityObelisk.class, new TESRObelisk());
    }



    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking() && hand == EnumHand.MAIN_HAND) {
            TileEntityObelisk te = getTileEntity(world, pos);
            te.hasTESR(!te.hasTESR());
        } else if (!player.isSneaking() && !world.isRemote) {
            IObelisk obelisk = player.getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
            boolean sendMessage = false;

            if (obelisk != null) {
                if (!obelisk.getObeliskPos().equals(pos)) {
                    obelisk.setObeliskPos(pos);
                    sendMessage = true;
                }
                if (obelisk.getObeliskDim() != player.world.provider.getDimension()) {
                    obelisk.setObeliskDim(player.world.provider.getDimension());
                    sendMessage = true;
                }
            }

            if (sendMessage) {
                Constants.Log.playerMessage(player, "Obelisk location set to: " + "X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ", in dimension: " + player.world.provider.getDimension());
            } else {
                Constants.Log.playerMessage(player, "Obelisk already defined as a spawn point!");
            }
        }
        return true;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            IObelisk obelisk = player.getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
            if (obelisk != null) {
                obelisk.setObeliskDim(player.world.provider.getDimension());
                obelisk.setObeliskPos(pos);

                TileEntityObelisk tile = getTileEntity(world, pos);
                tile.hasTESR(true);
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        IObelisk obelisk = player.getCapability(CapabilityObelisk.OBELISK_CAPABILITY, null);
        if (obelisk != null) {
            obelisk.setObeliskDim(Integer.MIN_VALUE);
            obelisk.setObeliskPos(BlockPos.ORIGIN);
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }


    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }
    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HAS_TESR);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityObelisk();
    }

    private TileEntityObelisk getTileEntity(IBlockAccess world, BlockPos pos) {
        return (TileEntityObelisk) world.getTileEntity(pos);
    }

}
