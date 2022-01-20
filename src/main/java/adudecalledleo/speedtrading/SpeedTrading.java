package adudecalledleo.speedtrading;

import adudecalledleo.speedtrading.config.ModConfig;
import adudecalledleo.speedtrading.config.TradeBlockBehavior;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

public class SpeedTrading implements ClientModInitializer {
    public static final String MOD_ID = "speedtrading";
    public static final String MOD_NAME = "Speed Trading";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        TradeBlockBehavior.registerConfigGuiProvider(ModConfig.class);
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        ModKeyBindings.register();
        ClientTickEvents.END_WORLD_TICK.register(SpeedTradeTimer::onClientWorldTick);
        LOGGER.info("Waste your hard-earned emeralds with ease!");
    }
}
