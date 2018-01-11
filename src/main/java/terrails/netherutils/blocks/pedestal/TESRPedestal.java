package terrails.netherutils.blocks.pedestal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TESRPedestal extends TileEntitySpecialRenderer<TileEntityPedestal> {

    @Override
    public void render(TileEntityPedestal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!te.getStack().isEmpty()) {
            ItemStack stack = te.getStack();
            EntityItem item = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack);
            item.hoverStart = 0.0F;
            GlStateManager.pushMatrix();
            float yDiff = MathHelper.sin((System.currentTimeMillis() % 86400000) / 1800F) * 0.1F + 0.1F;
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.65F + yDiff, (float) z + 0.5F);
            float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
            GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.75d, 0.75d, 0.75d);
            Minecraft.getMinecraft().getRenderManager().renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
            GlStateManager.popMatrix();
        }
    }
}
