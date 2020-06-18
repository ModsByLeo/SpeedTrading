package adudecalledleo.speedtrading;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
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

    public static boolean areStacksEqual(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && ItemStack.areTagsEqual(a, b) && a.getCount() >= b.getCount();
    }

    public static boolean listContainsStack(DefaultedList<ItemStack> list, ItemStack stack) {
        for (ItemStack itemStack : list)
            if (areStacksEqual(itemStack, stack))
                return true;
        return false;
    }

    public static boolean playerHasStack(PlayerInventory playerInventory, ItemStack stack) {
        return listContainsStack(playerInventory.main, stack);
    }
}
