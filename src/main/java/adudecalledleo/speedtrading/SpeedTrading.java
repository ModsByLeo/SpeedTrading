package adudecalledleo.speedtrading;

import adudecalledleo.lionutils.LoggerUtil;
import adudecalledleo.speedtrading.config.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

public class SpeedTrading implements ClientModInitializer {
    public static final String MOD_ID = "speedtrading";
    public static final String MOD_NAME = "Speed Trading";

    public static final Logger LOGGER = LoggerUtil.getLogger(MOD_NAME);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        LOGGER.info("Waste your hard-earned emeralds with ease!");
    }
}
