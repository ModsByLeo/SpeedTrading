package adudecalledleo.speedtrading;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpeedTradingMod implements ClientModInitializer {
    public static final String MOD_ID = "speedtrading";
    public static final String MOD_NAME = "Speed Trading";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        ClientTickCallback.EVENT.register(SpeedTradingAntiFreezeMeasure::onClientTick);
        log(Level.INFO, "Initialized client");
    }

    public static void log(Level level, String message){
        LOGGER.log(level, message);
    }

    public static boolean areStacksEqual(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && ItemStack.areTagsEqual(a, b) && a.getCount() >= b.getCount();
    }

    public static boolean listContainsStack(DefaultedList<ItemStack> list, ItemStack stack) {
        if (stack.isEmpty())
            return true;
        for (ItemStack itemStack : list)
            if (areStacksEqual(itemStack, stack))
                return true;
        return false;
    }

    public static boolean playerHasStack(PlayerInventory playerInventory, ItemStack stack) {
        return listContainsStack(playerInventory.main, stack);
    }

    public static boolean playerCanAcceptStack(PlayerInventory playerInventory, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        else if (stack.isDamaged())
            return playerInventory.getEmptySlot() >= 0;
        else
            return playerInventory.getOccupiedSlotWithRoomForStack(stack) >= 0;
    }
}
