package terrails.netherutils.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import terrails.netherutils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ConfigGUI extends GuiConfig {

    public ConfigGUI(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(),
                Constants.MOD_ID, false, false, "/" + Constants.MOD_ID + ".cfg");
    }

    private static List<IConfigElement> getConfigElements() {

        List<IConfigElement> list = new ArrayList<>();

        ConfigHandler.config.setCategoryLanguageKey(ConfigHandler.FIRST_SPAWN, "nu.config.starting.title");
        ConfigHandler.config.setCategoryLanguageKey(ConfigHandler.GENERAL_SETTINGS, "nu.config.general.title");
        ConfigHandler.config.setCategoryLanguageKey(ConfigHandler.FEATURES, "nu.config.features.title");
        ConfigHandler.config.setCategoryLanguageKey(ConfigHandler.FEATURES_PORTAL, "nu.config.features_portal.title");
        ConfigHandler.config.setCategoryLanguageKey(ConfigHandler.GENERATION, "nu.config.generation.title");
        ConfigHandler.config.setCategoryComment(ConfigHandler.FIRST_SPAWN, "Starting items and potion effects");
        ConfigHandler.config.setCategoryComment(ConfigHandler.GENERAL_SETTINGS, "General settings");
        ConfigHandler.config.setCategoryComment(ConfigHandler.FEATURES, "Edit the features of the mod");
        ConfigHandler.config.setCategoryComment(ConfigHandler.GENERATION, "Configure the wood generation");
        ConfigHandler.config.setCategoryComment(ConfigHandler.FEATURES_PORTAL, "Configure the portal settings");

        list.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.GENERAL_SETTINGS)).getChildElements());

        return list;
    }
}
