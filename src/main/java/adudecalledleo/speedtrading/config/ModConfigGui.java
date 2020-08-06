package adudecalledleo.speedtrading.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static adudecalledleo.speedtrading.SpeedTradingMod.CONFIG_HOLDER;

public class ModConfigGui {
    public static ConfigBuilder getConfigBuilder() {
        final ModConfig cfg = CONFIG_HOLDER.get();
        ConfigBuilder cb = ConfigBuilder.create().setTitle(t("title"));
        cb.setSavingRunnable(CONFIG_HOLDER::save);
        addGeneralCategory(cfg, cb);
        return cb;
    }

    private static final ModConfig DEFAULTS = new ModConfig();

    @SuppressWarnings("rawtypes")
    private static final Function<Enum, Text> TRADE_BLOCK_BEHAVIOR_NAME_PROVIDER = value -> {
        MutableText text = new TranslatableText(k("trade_block_behavior." + value.toString()
                                                                                            .toLowerCase()));
        if (value == ModConfig.TradeBlockBehavior.DISABLED)
            text = text.formatted(Formatting.RED);
        return text;
    };

    private static String k(String key) {
        return "speedtrading.config." + key;
    }

    private static Text t(String key) {
        return new TranslatableText(k(key));
    }

    private static Supplier<Optional<Text[]>> tooltip(final String key) {
        final Optional<Text[]> data = Optional.of(new Text[] { new TranslatableText(key) });
        return () -> data;
    }

    private static Supplier<Optional<Text[]>> tooltip(final String... keys) {
        final Optional<Text[]> data = Optional.of(Arrays.stream(keys).map(TranslatableText::new).toArray(Text[]::new));
        return () -> data;
    }

    @SuppressWarnings("SameParameterValue")
    private static Supplier<Optional<Text[]>> tooltip(final String key, final int count) {
        if (count == 1)
            return tooltip(key);
        return tooltip(IntStream.range(0, count).mapToObj(i -> String.format("%s[%d]", key, i)).toArray(String[]::new));
    }

    private static void addGeneralCategory(ModConfig cfg, ConfigBuilder cb) {
        ConfigEntryBuilder eb = cb.entryBuilder();
        ConfigCategory cGeneral = cb.getOrCreateCategory(t("category.general"));
        cGeneral.addEntry(eb.startIntSlider(t("ticks_between_actions"), cfg.ticksBetweenActions, 1, 100)
                         .setSaveConsumer(value -> cfg.ticksBetweenActions = value)
                         .setTooltipSupplier(tooltip(k("ticks_between_actions.tooltip"), 2))
                         .setDefaultValue(DEFAULTS.ticksBetweenActions).build());
        cGeneral.addEntry(eb.startEnumSelector(t("trade_block_behavior"),
                                               ModConfig.TradeBlockBehavior.class, cfg.tradeBlockBehavior)
                         .setSaveConsumer(value -> cfg.tradeBlockBehavior = value)
                         .setEnumNameProvider(TRADE_BLOCK_BEHAVIOR_NAME_PROVIDER)
                         .setTooltipSupplier(tooltip(k("trade_block_behavior.tooltip")))
                         .setDefaultValue(DEFAULTS.tradeBlockBehavior).build());
    }
}
