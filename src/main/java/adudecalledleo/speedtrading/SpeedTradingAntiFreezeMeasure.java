package adudecalledleo.speedtrading;

import net.minecraft.client.MinecraftClient;

public class SpeedTradingAntiFreezeMeasure {
    private static int counter;

    public static void reset() {
        counter = 0;
    }

    public static int getCounterMax() {
        return 1; // TODO make this configurable
    }

    public static boolean doAction() {
        return counter == getCounterMax();
    }

    public static void onClientTick(MinecraftClient mc) {
        counter++;
        if (counter > getCounterMax())
            reset();
    }
}
