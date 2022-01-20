package adudecalledleo.speedtrading.mixin;

import adudecalledleo.speedtrading.ModKeyBindings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.slot.Slot;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private double x;
    @Shadow private double y;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void updateModKeys(long window, int button, int action, int mods, CallbackInfo ci) {
        // this forces our key bindings to be updated in screens
        if (client.currentScreen != null && client.getWindow().getHandle() == window) {
            if (client.currentScreen instanceof HandledScreenAccessor handledScreen) {
                Slot focusedSlot = handledScreen.callGetSlotAt(x, y);
                if (focusedSlot != null) {
                    // mouse is over a slot, don't update keys!
                    return;
                }
            } else {
                Element hoveredElement = client.currentScreen.hoveredElement(x, y).orElse(null);
                if (hoveredElement instanceof ClickableWidget) {
                    // mouse is over something clickable, don't update keys!
                    return;
                }
            }

            KeyBinding targetBinding = null;
            for (KeyBinding keyBinding : ModKeyBindings.all) {
                if (keyBinding.matchesMouse(button)) {
                    targetBinding = keyBinding;
                    break;
                }
            }
            if (targetBinding == null)
                return;
            if (action == GLFW_RELEASE)
                targetBinding.setPressed(false);
            else {
                targetBinding.setPressed(true);
                ((KeyBindingAccessor) targetBinding).setTimesPressed(((KeyBindingAccessor) targetBinding).getTimesPressed() + 1);
            }
        }
    }
}
