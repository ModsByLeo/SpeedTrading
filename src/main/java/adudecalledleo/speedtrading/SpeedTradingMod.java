package adudecalledleo.speedtrading;

import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpeedTradingMod implements ClientModInitializer {
    public static final String MOD_ID = "speedtrading";
    public static final String MOD_NAME = "Speed Trading";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing client");
        // TODO initializer
    }

    public static void log(Level level, String message){
        LOGGER.log(level, message);
    }
}