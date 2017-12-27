package terrails.netherutils.client.render;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.tileentity.TileEntityTank;

@SideOnly(Side.CLIENT)
public class TESRTank extends TileEntitySpecialRenderer<TileEntityTank> {

    @Override
    public void render(TileEntityTank tile, double x, double y, double z, float partialTicks, int breakProgress, float alpha) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        if (tile.getTank().getFluid() != null) {
            Cuboid6 cuboid = new Cuboid6(0.06, 0.06, 0.06, 1 - 0.04, 1 - 0.04, 1 - 0.04).add(new Vector3(x, y, z));
            RenderUtils.renderFluidCuboidGL(tile.getTank().getFluid(), cuboid, tile.getTank().getFluidAmount() / (4D * Fluid.BUCKET_VOLUME), 0.75);
        }
    }
}
