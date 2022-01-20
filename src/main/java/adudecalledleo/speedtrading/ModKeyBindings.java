package adudecalledleo.speedtrading;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;

import static net.minecraft.client.util.InputUtil.GLFW_KEY_LEFT_ALT;

public class ModKeyBindings {
    private ModKeyBindings() { }

    public static final KeyBinding keyOverrideBlock = new KeyBinding("key.speedtrading.overrideBlock",
            GLFW_KEY_LEFT_ALT, KeyBinding.INVENTORY_CATEGORY);

    public static final KeyBinding[] all = new KeyBinding[] { keyOverrideBlock };

    public static void register() {
        for (KeyBinding keyBinding : all) {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }

    public static boolean isDown(KeyBinding keyBinding) {
        if (keyBinding.isUnbound())
            return false;
        return keyBinding.isPressed();
    }
}
