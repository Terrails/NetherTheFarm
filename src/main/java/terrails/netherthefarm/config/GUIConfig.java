package terrails.netherthefarm.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import terrails.netherthefarm.Constants;

import java.util.ArrayList;
import java.util.List;

public class GUIConfig extends GuiConfig {

    public GUIConfig(GuiScreen parentScreen) {
        super(parentScreen, GUIConfig.getConfigElements(),
                Constants.MOD_ID, false, false, "/" + Constants.MOD_ID + ".cfg");
    }

    @SuppressWarnings("deprecation")
    private static List<IConfigElement> getConfigElements() {

        List<IConfigElement> list = new ArrayList<IConfigElement>();
        List<IConfigElement> GENERAL_SETTINGS = new ConfigElement(ConfigHandler.configFile.getCategory(ConfigHandler.GENERAL_SETTINGS)).getChildElements();
        list.add(new DummyConfigElement.DummyCategoryElement("General Settings", "config.category.arrowSettings", GENERAL_SETTINGS));

        return list;
    }
}
