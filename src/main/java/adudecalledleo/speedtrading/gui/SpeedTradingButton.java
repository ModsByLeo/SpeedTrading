package adudecalledleo.speedtrading.gui;

import adudecalledleo.speedtrading.MerchantScreenAccess;
import adudecalledleo.speedtrading.SpeedTradingAntiFreezeMeasure;
import adudecalledleo.speedtrading.SpeedTradingMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;

import java.util.ArrayList;
import java.util.List;

public class SpeedTradingButton extends AbstractPressableButtonWidget {
    private static final LiteralText EMPTY = new LiteralText("");

    private final MerchantScreenAccess msa;
    private MerchantScreenAccess.TradeState cachedTradeState;
    private boolean trading;
    private boolean refill;

    public SpeedTradingButton(int x, int y, MerchantScreenAccess msa) {
        super(x, y, 18, 20, EMPTY);
        this.msa = msa;
        trading = false;
        refill = false;
        recacheState();
    }

    @Override
    public void onPress() {
        recacheState();
        if (!trading && cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM) {
            SpeedTradingAntiFreezeMeasure.reset();
            trading = true;
            refill = true;
        }
    }

    private void performTrade() {
        if (trading) {
            if (msa.isClosed()) {
                trading = false;
                return;
            }
            if (SpeedTradingAntiFreezeMeasure.doAction()) {
                if (refill)
                    msa.refillTradeSlots();
                else
                    msa.performTrade();
                refill = !refill;
                recacheState();
                trading = cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM;
                if (!trading) {
                    msa.clearTradeSlots();
                    recacheState();
                }
            }
        }
    }

    private static final Identifier BUTTON_LOCATION = new Identifier(SpeedTradingMod.MOD_ID, "textures/gui/speedtrade.png");

    public void recacheState() {
        cachedTradeState = msa.getTradeState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (msa.isClosed())
            return;
        if (cachedTradeState == MerchantScreenAccess.TradeState.NO_SELECTION)
            recacheState();
        performTrade();
        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(BUTTON_LOCATION);
        RenderSystem.color4f(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int i = 36;
        if (!trading && cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM)
            i = isHovered() ? 18 : 0;
        drawTexture(matrices, x, y, 0, trading ? 36 : i, 20, 18, 20, 54);
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        if (!isHovered())
            return;
        List<Text> textList = new ArrayList<>();
        if (trading)
            textList.add(new TranslatableText("speedtrading.tooltip.trading"));
        else
            textList.add(cachedTradeState.getText());
        TradeOffer currentOffer = msa.getCurrentTradeOffer();
        if (currentOffer != null)
            appendTradeDescription(currentOffer, textList);
        msa.renderTooltip(matrices, textList, mouseX, mouseY);
    }

    private void appendTradeDescription(TradeOffer offer, List<Text> destList) {
        ItemStack originalFirstBuyItem = offer.getOriginalFirstBuyItem();
        ItemStack adjustedFirstBuyItem = offer.getAdjustedFirstBuyItem();
        ItemStack secondBuyItem = offer.getSecondBuyItem();
        ItemStack sellItem = offer.getMutableSellItem();
        destList.add(new TranslatableText("speedtrading.config.current_trade.is")
                .styled(style -> style.withColor(Formatting.GRAY)));
        destList.add(createItemStackDescription(originalFirstBuyItem, adjustedFirstBuyItem));
        if (!secondBuyItem.isEmpty())
            destList.add(new TranslatableText("speedtrading.config.current_trade.and")
                    .append(createItemStackDescription(secondBuyItem))
                    .fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        destList.add(new TranslatableText("speedtrading.config.current_trade.for")
                .append(createItemStackDescription(sellItem))
                .fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

    private Text createItemStackDescription(ItemStack stack, ItemStack adjustedStack) {
        if (stack.getCount() == adjustedStack.getCount())
            return createItemStackDescription(stack);
        else {
            return new LiteralText("[")
                    .append(new TranslatableText(stack.getTranslationKey()))
                    .append(new LiteralText("] "))
                    .append(new LiteralText("x" + stack.getCount()).styled(style -> style.withFormatting(Formatting.RED, Formatting.STRIKETHROUGH)))
                    .append(new LiteralText(" "))
                    .append(new LiteralText("x" + adjustedStack.getCount()).styled(style -> style.withColor(Formatting.GREEN)))
                    .fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        }
    }

    private Text createItemStackDescription(ItemStack stack) {
        return new LiteralText("[")
                .append(new TranslatableText(stack.getTranslationKey()))
                .append(new LiteralText("] x" + stack.getCount()))
                .styled(style -> style.withColor(Formatting.WHITE));
    }
}
