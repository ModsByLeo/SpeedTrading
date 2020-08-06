package adudecalledleo.speedtrading;

import adudecalledleo.lionutils.ConfigHolder;
import adudecalledleo.lionutils.LoggerUtil;
import adudecalledleo.speedtrading.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class SpeedTradingMod implements ClientModInitializer {
    public static final String MOD_ID = "speedtrading";
    public static final String MOD_NAME = "Speed Trading";

    public static final Logger LOGGER = LoggerUtil.getLogger(MOD_NAME);
    public static final ConfigHolder<ModConfig> CONFIG_HOLDER = ConfigHolder.create(MOD_ID, ModConfig.class,
            ModConfig::new, ConfigHolder.createExceptionHandler(LOGGER));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(SpeedTradingAntiFreezeMeasure::onClientTick);
        log(Level.INFO, "Initialized client");
    }

    public static void log(Level level, String message){
        LOGGER.log(level, message);
    }

    public static boolean areItemsEqual(ItemStack a, ItemStack b) {
        return ItemStack.areItemsEqualIgnoreDamage(a, b) &&
               (!a.hasTag() || b.hasTag() && NbtHelper.matches(b.getTag(), a.getTag(), false));
    }

    public static boolean listContainsStack(DefaultedList<ItemStack> list, ItemStack stack) {
        if (stack.isEmpty())
            return true;
        int count = 0;
        for (ItemStack itemStack : list) {
            if (areItemsEqual(itemStack, stack))
                count += itemStack.getCount();
        }
        return count >= stack.getCount();
    }

    public static boolean playerHasStack(PlayerInventory playerInventory, ItemStack stack) {
        return listContainsStack(playerInventory.main, stack);
    }

    public static boolean playerCanAcceptStack(PlayerInventory playerInventory, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (!stack.isDamaged())
            if (playerInventory.getOccupiedSlotWithRoomForStack(stack) >= 0)
                return true;
        return playerInventory.getEmptySlot() >= 0;
    }
}
