package adudecalledleo.speedtrading.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;

public class PlayerInventoryUtil {
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
        if (stack.isStackable())
            if (playerInventory.getOccupiedSlotWithRoomForStack(stack) >= 0)
                return true;
        return playerInventory.getEmptySlot() >= 0;
    }
}
