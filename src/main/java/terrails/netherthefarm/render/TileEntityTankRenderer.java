package terrails.netherthefarm.render;

import codechicken.lib.fluid.FluidUtils;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import terrails.netherthefarm.tile.TileEntityTank;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer<TileEntityTank> {


    @Override
    public void render(TileEntityTank entityTank, double x, double y, double z, float partialTicks, int breakProgress, float alpha) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        renderLiquid(entityTank, x, y, z);
    }

    public static void renderLiquid(TileEntityTank tank, double x, double y, double z) {
        if (tank.getTank().getFluid() != null)
            RenderUtils.renderFluidCuboidGL(tank.getTank().getFluid(), new Cuboid6(0.05, 0.05, 0.05, 1 - 0.05, 1 - 0.05, 1 - 0.05).add(new Vector3(x, y, z)), tank.getTank().getFluidAmount() / (4D * FluidUtils.B), 0.75);
    }
}
