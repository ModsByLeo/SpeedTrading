package adudecalledleo.speedtrading.compat;

import adudecalledleo.speedtrading.SpeedTradingMod;
import adudecalledleo.speedtrading.config.ModConfigGui;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public String getModId() {
        return SpeedTradingMod.MOD_ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ModConfigGui.getConfigBuilder().setParentScreen(parent).build();
    }
}
