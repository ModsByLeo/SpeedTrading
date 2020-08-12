package adudecalledleo.speedtrading;

import net.minecraft.client.MinecraftClient;

import static adudecalledleo.speedtrading.SpeedTradingMod.CONFIG_HOLDER;

public class SpeedTradingAntiFreezeMeasure {
    private static int counter;

    public static void reset() {
        counter = 0;
    }

    public static int getCounterMax() {
        return CONFIG_HOLDER.get().ticksBetweenActions;
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
