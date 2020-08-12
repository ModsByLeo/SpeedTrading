package adudecalledleo.speedtrading;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;

import java.util.List;

public interface MerchantScreenAccess {
    enum TradeState {
        CAN_PERFORM, NO_SELECTION, OUT_OF_STOCK, NO_ROOM_FOR_SELL_ITEM, NOT_ENOUGH_BUY_ITEMS, BLOCKED;

        public Text getText() {
            return new TranslatableText("speedtrading.tooltip." + name().toLowerCase())
                    .styled(style -> this == CAN_PERFORM
                            ? style
                            : style.withFormatting(Formatting.RED, Formatting.BOLD));
        }
    }

    boolean isClosed();
    TradeOffer getCurrentTradeOffer();
    TradeState getTradeState();
    void performTrade();
    void refillTradeSlots();
    void clearTradeSlots();
    void renderTooltip(MatrixStack matrices, List<Text> text, int mouseX, int mouseY);
}
