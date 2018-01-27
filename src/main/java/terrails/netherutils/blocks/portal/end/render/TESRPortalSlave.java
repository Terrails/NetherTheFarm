package terrails.netherutils.blocks.portal.end.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import terrails.netherutils.Constants;
import terrails.netherutils.blocks.portal.end.TileEntityPortalSlave;

public class TESRPortalSlave extends TileEntitySpecialRenderer<TileEntityPortalSlave> {

    private final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/magic/magic_circle.png");

    @Override
    public void render(TileEntityPortalSlave te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        boolean isGamePaused = Minecraft.getMinecraft().isGamePaused();
        boolean isPlayerInRange = getWorld().isAnyPlayerWithinRangeAt(te.getPos().getX() + 0.5, te.getPos().getY() + 0.75, te.getPos().getZ() + 0.5, 0.5);

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        if (te.isActive()) {
            if (isPlayerInRange) {
                if (!isGamePaused) {
                    te.counterCircle.increment();
                    te.counterTeleport.increment();
                }

                renderTeleportCircleTop(te.isAtPosTopMiddleCircle, te.counterTeleport.value());
                if (te.isAtPosTopMiddleCircle) {
                    renderTeleportCircleBottom(te.counterTeleport.value());
                }
                renderTeleportCircleSide(1, 1, te.counterCircle.value(), te);
                renderTeleportCircleSide(1, -1, te.counterCircle.value(), te);
                renderTeleportCircleSide(-1, 1, te.counterCircle.value(), te);
                renderTeleportCircleSide(-1, -1, te.counterCircle.value(), te);

                if (te.isAtPosSideCircles) {
                    renderParticles(te);
                }

                if (te.counterCircle.value() >= 800) {
                    te.counterCircle.clear();
                }

                if (te.counterTeleport.value() >= 720) {
                    if (te.isAtPosTopMiddleCircle) {
                        te.isReadyToTeleport = true;
                        te.sendReadyToTeleport();
                    }
                    te.isAtPosTopMiddleCircle = true;
                    te.counterTeleport.clear();
                }
            } else {
                renderActivatedCircle();
                te.counterCircle.clear();
                te.counterTeleport.clear();
                te.isAtPosSideCircles = false;
                te.isAtPosTopMiddleCircle = false;
            }
        }
        else if (!te.isActive()) {
            te.counterCircle.clear();
            te.counterTeleport.clear();
            te.isAtPosTopMiddleCircle = false;
            te.isAtPosSideCircles = false;
        }

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderTeleportCircleTop(boolean isAtPosTopMiddleCircle, float counterTeleport) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        GlStateManager.translate(0.5f, /*1*/0.6, 0.5f);
        if (isAtPosTopMiddleCircle) {
            GlStateManager.translate(0, 2.1, 0);
            GlStateManager.translate(0, -counterTeleport / 345, 0);
        }
        else {
            GlStateManager.translate(0, counterTeleport / 345, 0);
        }

        GlStateManager.scale(0.75f, 0, 0.75f);

        if (isAtPosTopMiddleCircle) {
            GlStateManager.scale((720 - counterTeleport) / 400, 0, (720 - counterTeleport) / 400);
        }
        else {
            GlStateManager.scale(counterTeleport / 400, 0, counterTeleport / 400);
        }

        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(counterTeleport/2, 0, 0, 1);

        GlStateManager.blendFunc(770, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_LOCATION);
        shadeModel();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(1, 1, 0.0D).tex(1.0D, 1).endVertex();
        buffer.pos(1, -1, 0.0D).tex(1.0D, 0).endVertex();
        buffer.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        buffer.pos(-1, 1, 0.0D).tex(0, 1).endVertex();

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
    private void renderTeleportCircleBottom(float counterTeleport) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        GlStateManager.translate(0.5f, /*1*/0.6, 0.5f);
        GlStateManager.translate(0, counterTeleport/345, 0);

        GlStateManager.scale(0.75f, 0, 0.75f);
        GlStateManager.scale(counterTeleport/400, 0, counterTeleport/400);

        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(counterTeleport/2, 0, 0, 1);

        GlStateManager.blendFunc(770, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_LOCATION);
        shadeModel();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(1, 1, 0.0D).tex(1.0D, 1).endVertex();
        buffer.pos(1, -1, 0.0D).tex(1.0D, 0).endVertex();
        buffer.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        buffer.pos(-1, 1, 0.0D).tex(0, 1).endVertex();

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderTeleportCircleSide(double posX, double posZ, float counterCircle, TileEntityPortalSlave te) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (te.isAtPosSideCircles) {
            GlStateManager.translate(0.5f + posX, /*2*/1.5, 0.5f + posZ);
            GlStateManager.scale(0.25f, 0, 0.25f);
        } else {
            double translate = /* 0.51 */ 0.65 + (counterCircle / /*322*/ 360);
            if (translate >= /*2*/1.5) te.isAtPosSideCircles = true;
            GlStateManager.translate(0.5f + posX, translate, 0.5f + posZ);

            double scale = (counterCircle - 800) / 2000/*1250*/;
            if (scale >= -0.25) GlStateManager.scale(0.25f, 0, 0.25f);
            else GlStateManager.scale(scale, 0, scale);
        }
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate((System.currentTimeMillis() / 20) % 360, 0, 0, 1);

        GlStateManager.blendFunc(770, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_LOCATION);
        shadeModel();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(1, 1, 0.0D).tex(1.0D, 1).endVertex();
        buffer.pos(1, -1, 0.0D).tex(1.0D, 0).endVertex();
        buffer.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        buffer.pos(-1, 1, 0.0D).tex(0, 1).endVertex();

        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderActivatedCircle() {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.translate(0.5F, /*1*/0.6F, 0.5F);
        GlStateManager.scale(0.5F, 0, 0.5F);

        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate((System.currentTimeMillis() / 20) % 360, 0, 0, 1);

        GlStateManager.blendFunc(770, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_LOCATION);
        shadeModel();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(1, 1, 0.0D).tex(1.0D, 1).endVertex();
        buffer.pos(1, -1, 0.0D).tex(1.0D, 0).endVertex();
        buffer.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        buffer.pos(-1, 1, 0.0D).tex(0, 1).endVertex();

        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderParticles(TileEntityPortalSlave te) {
        double xPos = te.getPos().getX() + 0.5;
        double yPos = te.getPos().getY() + /*0.6*/0.1;
        double zPos = te.getPos().getZ() + 0.5;
        for (int i = 0; i < 2; ++i)
        {
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos, yPos, zPos, 1, 0.4, 1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos, yPos, zPos, 1, 0.4, -1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos, yPos, zPos, -1, 0.4, -1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos, yPos, zPos, -1, 0.4, 1);
        }
    }

    private void shadeModel() {
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }
}
