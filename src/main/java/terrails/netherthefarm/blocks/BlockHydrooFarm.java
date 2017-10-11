package terrails.netherthefarm.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.NetherTheFarm;
import terrails.terracore.block.BlockBase;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class BlockHydrooFarm extends BlockBase {

    public BlockHydrooFarm(String name) {
        super(Material.WATER, name);
        setHardness(0.5F);
        setCreativeTab(Constants.NTF_TAB);
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }
}
