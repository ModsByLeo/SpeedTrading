package adudecalledleo.speedtrading.gui;

import adudecalledleo.speedtrading.SpeedTradeTimer;
import adudecalledleo.speedtrading.SpeedTrading;
import adudecalledleo.speedtrading.duck.MerchantScreenHooks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.village.TradeOffer;

import java.util.ArrayList;
import java.util.Locale;

public class SpeedTradeButton extends AbstractPressableButtonWidget implements Tickable {
    private static final int PHASE_INACTIVE = 0;
    private static final int PHASE_AUTOFILL = 1;
    private static final int PHASE_PERFORM = 2;

    private final MerchantScreenHooks hooks;
    private int phase;

    public SpeedTradeButton(int x, int y, MerchantScreenHooks hooks) {
        super(x, y, 18, 20, LiteralText.EMPTY);
        this.hooks = hooks;
        phase = PHASE_INACTIVE;
    }

    @Override
    public void onPress() {
        if (phase == PHASE_INACTIVE && hooks.getState() == MerchantScreenHooks.State.CAN_PERFORM) {
            phase++;
            SpeedTradeTimer.reset();
        }
    }

    private boolean checkState() {
        if (hooks.getState() != MerchantScreenHooks.State.CAN_PERFORM) {
            phase = PHASE_INACTIVE;
            hooks.clearSellSlots();
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        if (phase > PHASE_INACTIVE) {
            if (SpeedTradeTimer.doAction()) {
                if (!checkState())
                    return;
                switch (phase) {
                case PHASE_AUTOFILL:
                    hooks.autofillSellSlots();
                    phase++;
                    break;
                case PHASE_PERFORM:
                    hooks.performTrade();
                default:
                    phase = PHASE_AUTOFILL;
                    break;
                }
                checkState();
            }
        }
    }

    private static final Identifier BUTTON_LOCATION = SpeedTrading.id("textures/gui/speedtrade.png");

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(BUTTON_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int v = 36;
        if (phase == PHASE_INACTIVE && hooks.getState() == MerchantScreenHooks.State.CAN_PERFORM)
            v = isHovered() ? 18 : 0;
        drawTexture(matrices, x, y, 0, v, 20, 18, 20, 54);
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        if (!isHovered())
            return;
        ArrayList<Text> textList = new ArrayList<>();
        if (phase > PHASE_INACTIVE) {
            textList.add(new TranslatableText("speedtrading.tooltip.in_progress").styled(
                    style -> style.withFormatting(Formatting.BOLD, Formatting.ITALIC, Formatting.DARK_GREEN)
            ));
        } else {
            MerchantScreenHooks.State state = hooks.getState();
            if (state == MerchantScreenHooks.State.CAN_PERFORM) {
                textList.add(new TranslatableText("speedtrading.tooltip.can_perform").styled(
                        style -> style.withFormatting(Formatting.BOLD, Formatting.GREEN)
                ));
            } else {
                textList.add(new TranslatableText("speedtrading.tooltip.cannot_perform").styled(
                        style -> style.withFormatting(Formatting.BOLD, Formatting.RED)
                ));
                textList.add(
                        new TranslatableText("speedtrading.tooltip." + state.name().toLowerCase(Locale.ROOT)).styled(
                                style -> style.withFormatting(Formatting.ITALIC, Formatting.GRAY)
                        ));
            }
            appendTradeDescription(hooks.getCurrentTradeOffer(), textList);
        }
        hooks.callRenderTooltip(matrices, textList, mouseX, mouseY);
    }

    private static final Style STYLE_GRAY = Style.EMPTY.withColor(Formatting.GRAY);

    private void appendTradeDescription(TradeOffer offer, ArrayList<Text> destList) {
        if (offer == null)
            return;
        ItemStack originalFirstBuyItem = offer.getOriginalFirstBuyItem();
        ItemStack adjustedFirstBuyItem = offer.getAdjustedFirstBuyItem();
        ItemStack secondBuyItem = offer.getSecondBuyItem();
        ItemStack sellItem = offer.getMutableSellItem();
        destList.add(new TranslatableText("speedtrading.tooltip.current_trade.is")
                .styled(style -> style.withColor(Formatting.GRAY)));
        destList.add(createItemStackDescription(originalFirstBuyItem, adjustedFirstBuyItem)
                .fillStyle(STYLE_GRAY));
        if (!secondBuyItem.isEmpty())
            destList.add(new TranslatableText("speedtrading.tooltip.current_trade.and",
                    createItemStackDescription(secondBuyItem))
                    .fillStyle(STYLE_GRAY));
        destList.add(new TranslatableText("speedtrading.tooltip.current_trade.for",
                createItemStackDescription(sellItem))
                .fillStyle(STYLE_GRAY));
    }

    private MutableText createItemStackDescription(ItemStack stack, ItemStack adjustedStack) {
        if (stack.getCount() == adjustedStack.getCount())
            return createItemStackDescription(stack);
        else {
            return getItemStackName(stack)
                    .append(new LiteralText(" "))
                    .append(new LiteralText("x" + stack.getCount())
                            .styled(style -> style.withFormatting(Formatting.STRIKETHROUGH, Formatting.RED)))
                    .append(new LiteralText(" x" + adjustedStack.getCount())
                            .styled(style -> style.withFormatting(Formatting.BOLD, Formatting.GREEN)));
        }
    }

    private MutableText createItemStackDescription(ItemStack stack) {
        return getItemStackName(stack)
                .append(new LiteralText(" x" + stack.getCount()));
    }

    private MutableText getItemStackName(ItemStack stack) {
        return Texts.bracketed(new LiteralText("").append(stack.getName()).styled(style -> style.withFormatting(stack.getRarity().formatting)));
    }
}
