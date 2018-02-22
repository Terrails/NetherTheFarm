package terrails.netherutils.gui.client.portal.end;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import terrails.netherutils.Constants;
import terrails.netherutils.blocks.portal.end.TileEntityPortalMaster;
import terrails.netherutils.gui.inventory.portal.end.PortalContainer;

import java.util.ArrayList;
import java.util.List;

public class GuiPortal extends GuiContainer {

    private TileEntityPortalMaster tile;
    private final int START = 0;
    private final int STOP = 1;

    private static final String PATH = "textures/gui/";
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, PATH + "container/portal_container.png");

    public GuiPortal(IInventory playerInv, TileEntityPortalMaster tile) {
        super(new PortalContainer(playerInv, tile));

        xSize = 176;
        ySize = 166;

        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        if (tile.counterFluidTransfer.value() != -1 && tile.getFuelAmount() < tile.getFuelCapacity()) {
            int i = (int) this.tile.counterFluidTransfer.value() * 5 / 40;
            drawTexturedModalRect(xPos + 31 - i, yPos + 29, 2, 167, i, 4);
        }

        mc.getTextureManager().bindTexture(new ResourceLocation(Constants.MOD_ID, PATH + "widgets.png"));
        if (tile.isActivating) {
            this.drawTexturedModalRect(xPos + 143, yPos + 37, 40, 24, 24, 12);
        } else if (tile.isActive()) {
            this.drawTexturedModalRect(xPos + 143, yPos + 37, 40, 12, 24, 12);
        } else {
            this.drawTexturedModalRect(xPos + 143, yPos + 37, 40, 0, 24, 12);
        }

        if (tile.getFuel() != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            String fluid = tile.getFuel().getFluid().getStill().toString();

            TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
            TextureAtlasSprite sprite = map.getTextureExtry(fluid) != null ? map.getTextureExtry(fluid) : map.registerSprite(tile.getFuel().getFluid().getStill());

            int i = this.tile.getFuelAmount() * 52 / this.tile.getFuelCapacity();
            drawTexturedModalRect(xPos + 7, yPos + 69 - i, sprite, 16, 16 - (16 - i));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(GUI_TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (isPointInRegion(7, 17, 16, 53, mouseX, mouseY)) {
            List<String> list = new ArrayList<>();
            list.add(new TextComponentTranslation("nu.gui.fuel", tile.getFuelAmount()).getFormattedText());
            this.drawHoveringText(list, mouseX - x, mouseY - y);
        }
        if (isPointInRegion(143, 37, 24, 12, mouseX, mouseY)) {
            String translation = tile.isActivating ? I18n.format("nu.gui.status3") : tile.isActive() ? I18n.format("nu.gui.status1") : I18n.format("nu.gui.status2");
            List<String> list = new ArrayList<>();
            list.add(new TextComponentTranslation(translation, tile.isActive()).getFormattedText());
            this.drawHoveringText(list, mouseX - x, mouseY - y);
        }

        for (GuiButton guibutton : this.buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;

        ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Constants.MOD_ID, PATH + "widgets.png");
        CustomButton START_BUTTON = new CustomButton(BUTTON_TEXTURE, START, xPos + 118, yPos + 18, 0, 0);
        CustomButton STOP_BUTTON = new CustomButton(BUTTON_TEXTURE, STOP, xPos + 118, yPos + 48, 20, 0);

        buttonList.add(START_BUTTON);
        buttonList.add(STOP_BUTTON);

        this.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == START && !tile.isActive() && tile.hasFuel() && tile.hasRequiredBlocks() && mc.world.provider.getDimension() == 0) {
            this.tile.isActivating = true;
            this.tile.sendActivation();
        } else if (button.id == STOP && (tile.isActive() || tile.isActivating)) {
            this.tile.isActivating = false;
            this.tile.isActive(false);
            this.tile.sendActivation();
            this.tile.sendActive();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
        super.initGui();
    }

    @Override
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);
    }

    class CustomButton extends GuiButton {

        ResourceLocation location;

        int xStart;
        int yStart;

        public CustomButton(ResourceLocation location, int buttonId, int xPos, int yPos, int xStart, int yStart) {
            super(buttonId, xPos, yPos, 20, 20, "");
            this.location = location;
            this.xStart = xStart;
            this.yStart = yStart;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.renderEngine.bindTexture(location);
                drawTexturedModalRect(x, y, xStart, yStart, width, height);

                hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

                int i = yStart;

                if (tile.isActivating && this.id == START) {
                    i += this.width * 2;
                }
                else if (tile.isActive() && this.id == START) {
                    i += this.width * 2;
                }
                else if (!tile.isActive() && !tile.isActivating && this.id == STOP) {
                    i += this.width * 2;
                }
                else if (this.hovered) {
                    i += this.width;
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(location);

                this.drawTexturedModalRect(x, y, xStart, i, this.width, this.height);

            }
        }
        public void drawButtonForegroundLayer(int mouseX, int mouseY) {
            switch (this.id) {
                case START:
                    if (tile.isActivating) GuiPortal.this.drawHoveringText(I18n.format("nu.gui.start3"), mouseX, mouseY);
                    else if (tile.isActive()) GuiPortal.this.drawHoveringText(I18n.format("nu.gui.start2"), mouseX, mouseY);
                    else GuiPortal.this.drawHoveringText(I18n.format("nu.gui.start1"), mouseX, mouseY);
                    break;
                case STOP:
                    if (tile.isActivating) GuiPortal.this.drawHoveringText(I18n.format("nu.gui.stop3"), mouseX, mouseY);
                    else if (!tile.isActive()) GuiPortal.this.drawHoveringText(I18n.format("nu.gui.stop2"), mouseX, mouseY);
                    else GuiPortal.this.drawHoveringText(I18n.format("nu.gui.stop1"), mouseX, mouseY);
                    break;
            }
        }
    }
}