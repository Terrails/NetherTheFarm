package terrails.netherutils.blocks.portal.end;

import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import terrails.netherutils.config.ConfigHandler;

public class BlockPortalOverride extends BlockEndPortal {

    public BlockPortalOverride() {
        super(Material.PORTAL);
        this.setRegistryName(new ResourceLocation("minecraft", "end_portal"));
        this.setHardness(-1.0F);
        this.setResistance(6000000.0F);
        this.setLightLevel(1.0F);
        GameRegistry.registerTileEntity(TileEntityEndPortal.class, "end_portal");
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (ConfigHandler.useVanillaEndPortal || worldIn.provider.getDimension() == 1) {
            super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
        }
    }
}
