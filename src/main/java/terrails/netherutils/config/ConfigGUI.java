package terrails.netherutils.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import terrails.netherutils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ConfigGUI extends GuiConfig {

    public ConfigGUI(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Constants.MOD_ID, false, false, "/" + Constants.MOD_ID + ".cfg");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new DummyCategoryElement(new TextComponentTranslation("nu.config.starting", "").getFormattedText(), "nu.config.starting", FirstSpawn.class));
        list.add(new DummyCategoryElement(new TextComponentTranslation("nu.config.features", "").getFormattedText(), "nu.config.features", Features.class));
        list.add(new DummyCategoryElement(new TextComponentTranslation("nu.config.generation", "").getFormattedText(), "nu.config.generation", Generation.class));
        return list;
    }
    public static class FirstSpawn extends GuiConfigEntries.CategoryEntry {
        public FirstSpawn(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.FIRST_SPAWN)).getChildElements(), owningScreen.modID, false, false, "/" + Constants.MOD_ID + ".cfg");
        }
    }
    public static class Features extends GuiConfigEntries.CategoryEntry {
        public Features (GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, false, false, "/" + Constants.MOD_ID + ".cfg");
        }

        private static List<IConfigElement> getConfigElements() {
            ConfigHandler.config.getCategory(ConfigHandler.FEATURES_PORTAL).setLanguageKey("nu.config.features_portal");
            ConfigHandler.config.getCategory(ConfigHandler.FEATURES_NETHER_PORTAL).setLanguageKey("nu.config.features_nether_portal");
            ConfigHandler.config.getCategory(ConfigHandler.FEATURES_END_PORTAL).setLanguageKey("nu.config.features_end_portal");
            ConfigHandler.config.getCategory(ConfigHandler.FEATURES_TANK).setLanguageKey("nu.config.features_tank");
            ConfigHandler.config.getCategory(ConfigHandler.FEATURES_NETHER).setLanguageKey("nu.config.features_nether");
            return (new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.FEATURES)).getChildElements());
        }
    }
    public static class Generation extends GuiConfigEntries.CategoryEntry {
        public Generation (GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.GENERATION)).getChildElements(), owningScreen.modID, false, false, "/" + Constants.MOD_ID + ".cfg");
        }
    }
}
