package terrails.netherthefarm.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import terrails.netherthefarm.blocks.ModBlocks;
import terrails.netherthefarm.blocks.BlockObelisk;
import terrails.netherthefarm.tile.TileEntityObelisk;

@SideOnly(Side.CLIENT)
public class TileEntityObeliskRenderer extends TileEntitySpecialRenderer<TileEntityObelisk> {

    @Override
    public void render(TileEntityObelisk te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.hasTESR()) {
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();

            // Translate to the location of our tile entity
            GlStateManager.translate(x, y, z);
            GlStateManager.disableRescaleNormal();

            renderHandle(te, 0.05, 1.3, 0.05, 180, -1, 0, -1);
            renderHandle(te, 1-0.05, 1.3, 1-0.05, 180, 1, 0, -1);
            renderHandle(te, 1-0.05, 1.3, 0.05, 180, 0, 0, -1);
            renderHandle(te, 0.05, 1.3, 1-0.05, 180, 1, 0, 0);

            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }

    private void renderHandle(TileEntityObelisk te, double x, double y, double z, float rotateAngle, float rotateX, float rotateY, float rotateZ) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();


        long angle = (System.currentTimeMillis() / 10) % 400;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(rotateAngle, rotateX, rotateY, rotateZ);

        if (angle <= 200) {
            GlStateManager.translate(0F, angle / 3000F, 0F);
        } else {
            long angle2 = 400 - angle;
            GlStateManager.translate(0F, angle2 / 3000F, 0F);
        }

        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = te.getWorld();
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        IBlockState state = ModBlocks.OBELISK.getDefaultState().withProperty(BlockObelisk.IS_HANDLES, true);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(state);
        dispatcher.getBlockModelRenderer().renderModel(world, model, state, te.getPos(), bufferBuilder, true);

        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}