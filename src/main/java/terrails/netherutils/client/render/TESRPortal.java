package terrails.netherutils.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import terrails.netherutils.Constants;
import terrails.netherutils.tileentity.portal.TileEntityPortalMaster;

public class TESRPortal extends TileEntitySpecialRenderer<TileEntityPortalMaster> {

    private final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/magic/magic_circle.png");

    @Override
    public void render(TileEntityPortalMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        boolean isGamePaused = !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().isIntegratedServerRunning();
        boolean isPlayerInRange = getWorld().isAnyPlayerWithinRangeAt(te.getPos().getX() + 0.5, te.getPos().getY() + 1, te.getPos().getZ() + 0.5, 0.5);

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        if (te.isActivating) {
            if (isGamePaused) te.counterActivation++;
            renderActivationSequence(te);
            if (te.counterActivation >= 800) {
                te.counterActivation = 0;
            }
        }
        else if (te.isActive()) {
            if (isPlayerInRange) {
                if (isGamePaused) {
                    te.counterCircle++;
                    te.counterTeleport++;
                }

                renderTeleportCircleTop(te);
                if (te.isAtPosition)
                    renderBottomTeleportCircle(te);
                renderSideTeleportCircles(1, 1, te);
                renderSideTeleportCircles(1, -1, te);
                renderSideTeleportCircles(-1, 1, te);
                renderSideTeleportCircles(-1, -1, te);

                if (te.isAtPosition2) {
                    renderParticles(te);
                }

                if (te.counterCircle >= 500) {
                    te.counterCircle = 0;
                }
                if (te.counterTeleport >= 360) {
                    te.isAtPosition = true;
                    te.counterTeleport = 0;
                }
            } else {
                renderActivatedCircle();
                te.counterCircle = 0;
                te.counterTeleport = 0;
                te.isAtPosition = false;
                te.isAtPosition2 = false;
            }
            te.counterActivation = 0;
        } else if (!te.isActive()) {
            te.counterActivation = 0;
            te.counterCircle = 0;
            te.counterTeleport = 0;
            te.isAtPosition = false;
            te.isAtPosition2 = false;
        }
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderTeleportCircleTop(TileEntityPortalMaster te) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        GlStateManager.translate(0.5f, 1, 0.5f);
        if (te.isAtPosition) {
            GlStateManager.translate(0, 2.1, 0);
            GlStateManager.translate(0, -te.counterTeleport / 175, 0);
        }
        else {GlStateManager.translate(0, te.counterTeleport / 175, 0);}

        GlStateManager.scale(0.5f, 0, 0.5f);

        if (te.isAtPosition) {GlStateManager.scale((360 - te.counterTeleport) / 145, 0, (360 - te.counterTeleport) / 145);}
        else {GlStateManager.scale(te.counterTeleport / 145, 0, te.counterTeleport / 145);}

        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(te.counterTeleport, 0, 0, 1);

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
    private void renderBottomTeleportCircle(TileEntityPortalMaster te) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        GlStateManager.translate(0.5f, 1, 0.5f);
        GlStateManager.translate(0, te.counterTeleport/175, 0);

        GlStateManager.scale(0.5f, 0, 0.5f);
        GlStateManager.scale(te.counterTeleport/145, 0, te.counterTeleport/145);

        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(te.counterTeleport, 0, 0, 1);

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
    private void renderSideTeleportCircles(double posX, double posZ, TileEntityPortalMaster te) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (te.isAtPosition2) {
            GlStateManager.translate(0.5f + posX, 2, 0.5f + posZ);
            GlStateManager.scale(0.25f, 0, 0.25f);
        } else {
            double translate = 0.51 + (te.counterCircle / 215);
            if (translate >= 2) te.isAtPosition2 = true;
            GlStateManager.translate(0.5f + posX, translate, 0.5f + posZ);

            double scale = (te.counterCircle - 500) / 750;
            if (scale >= -0.25) GlStateManager.scale(0.25f, 0, 0.25f);
            else GlStateManager.scale(scale, 0, scale);
        }

        float angle = (System.currentTimeMillis() / 20) % 360;
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(angle, 0, 0, 1);

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

    private void renderActivationSequence(TileEntityPortalMaster te) {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        GlStateManager.blendFunc(770, 1);

        GlStateManager.translate(0.5F, 0.1F, 0.5F);
        GlStateManager.scale(1.5F, 1F, 1.5F);

        float translationCounter = te.counterActivation / 900;
        GlStateManager.translate(0, translationCounter, 0);

        float scaleCounter = Math.abs((te.counterActivation) - 800) / 1000;
        GlStateManager.scale(scaleCounter + 0.35F, 1, scaleCounter + 0.35F);

        long angle = (System.currentTimeMillis() / 20) % 360;
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(angle, 0, 0, 1);

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
    private void renderActivatedCircle() {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.translate(0.5f, 1, 0.5f);
        GlStateManager.scale(0.5f, 0, 0.5f);

        float counter = (System.currentTimeMillis() / 20) % 360;
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate(counter, 0, 0, 1);

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

    private void renderParticles(TileEntity te) {
        double xPos = te.getPos().getX();
        double yPos = te.getPos().getY();
        double zPos = te.getPos().getZ();
        for (int i = 0; i < 2; ++i)
        {
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos + 0.5, yPos + 0.6, zPos + 0.5, 1, 0.4, 1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos + 0.5, yPos + 0.6, zPos + 0.5, 1, 0.4, -1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos + 0.5, yPos + 0.6, zPos + 0.5, -1, 0.4, -1);
            getWorld().spawnParticle(EnumParticleTypes.PORTAL, xPos + 0.5, yPos + 0.6, zPos + 0.5, -1, 0.4, 1);
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