package adudecalledleo.speedtrading.config;

import java.util.Locale;

import adudecalledleo.speedtrading.SpeedTrading;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

@Config(name = SpeedTrading.MOD_ID)
public class ModConfig implements ConfigData {
    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @ConfigEntry.BoundedDiscrete(max = 100, min = 1)
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int ticksBetweenActions = 1;

    public enum TradeBlockBehavior implements SelectionListEntry.Translatable {
        DAMAGEABLE, UNSTACKABLE, DISABLED;

        @Override
        public @NotNull String getKey() {
            return "text.autoconfig.speedtrading.option.tradeBlockBehavior." + this.name().toLowerCase(Locale.ROOT);
        }
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip
    public TradeBlockBehavior tradeBlockBehavior = TradeBlockBehavior.DAMAGEABLE;
}
