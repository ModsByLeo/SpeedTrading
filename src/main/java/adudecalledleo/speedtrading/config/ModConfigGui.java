package adudecalledleo.speedtrading.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ModConfigGui {
    public static ConfigBuilder getConfigBuilder() {
        final ModConfig cfg = ModConfigHolder.getConfig();
        ConfigBuilder cb = ConfigBuilder.create().setTitle(k("title"));
        cb.setSavingRunnable(ModConfigHolder::saveConfig);
        addGeneralCategory(cfg, cb);
        return cb;
    }

    private static final ModConfig DEFAULTS = new ModConfig();

    @SuppressWarnings("rawtypes")
    private static final Function<Enum, String> TRADE_BLOCK_BEHAVIOR_NAME_PROVIDER = value -> {
        String text = I18n.translate(k("trade_block_behavior." + value.toString().toLowerCase()));
        if (value == ModConfig.TradeBlockBehavior.DISABLED)
            text = Formatting.RED.toString() + text;
        return text;
    };

    private static String k(String key) {
        return "speedtrading.config." + key;
    }

    private static Supplier<Optional<String[]>> tooltip(final String key) {
        final Optional<String[]> data = Optional.of(new String[] { I18n.translate(key) });
        return () -> data;
    }

    private static Supplier<Optional<String[]>> tooltip(final String... keys) {
        final Optional<String[]> data = Optional.of(Arrays.stream(keys).map(I18n::translate).toArray(String[]::new));
        return () -> data;
    }

    @SuppressWarnings("SameParameterValue")
    private static Supplier<Optional<String[]>> tooltip(final String key, final int count) {
        if (count == 1)
            return tooltip(key);
        return tooltip(IntStream.range(0, count).mapToObj(i -> String.format("%s[%d]", key, i)).toArray(String[]::new));
    }

    private static void addGeneralCategory(ModConfig cfg, ConfigBuilder cb) {
        ConfigEntryBuilder eb = cb.entryBuilder();
        ConfigCategory cGeneral = cb.getOrCreateCategory(k("category.general"));
        cGeneral.addEntry(eb.startIntSlider(k("ticks_between_actions"), cfg.ticksBetweenActions, 1, 100)
                         .setSaveConsumer(value -> cfg.ticksBetweenActions = value)
                         .setTooltipSupplier(tooltip(k("ticks_between_actions.tooltip"), 2))
                         .setDefaultValue(DEFAULTS.ticksBetweenActions).build());
        cGeneral.addEntry(eb.startEnumSelector(k("trade_block_behavior"),
                                               ModConfig.TradeBlockBehavior.class, cfg.tradeBlockBehavior)
                         .setSaveConsumer(value -> cfg.tradeBlockBehavior = value)
                         .setEnumNameProvider(TRADE_BLOCK_BEHAVIOR_NAME_PROVIDER)
                         .setTooltipSupplier(tooltip(k("trade_block_behavior.tooltip")))
                         .setDefaultValue(DEFAULTS.tradeBlockBehavior).build());
    }
}
