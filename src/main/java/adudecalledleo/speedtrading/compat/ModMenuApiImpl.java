package adudecalledleo.speedtrading.compat;

import adudecalledleo.speedtrading.config.ModConfigGui;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ModConfigGui.getConfigBuilder().setParentScreen(parent).build();
    }
}
