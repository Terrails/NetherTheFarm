package terrails.netherutils.blocks.portal.nether;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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
import terrails.netherutils.blocks.portal.nether.render.TESRPortalSlave;
import terrails.terracore.block.BlockTileEntity;
import terrails.terracore.registry.client.ICustomModel;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class BlockPortalSlave extends BlockTileEntity<TileEntityPortalSlave> implements ICustomModel {

    public BlockPortalSlave(String name) {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        setUnlocalizedName(NetherUtils.MOD_ID + "." + name);
        setBlockUnbreakable();
        GameRegistry.registerTileEntity(TileEntityPortalSlave.class, name);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalSlave.class, new TESRPortalSlave());
        ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, location);
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
