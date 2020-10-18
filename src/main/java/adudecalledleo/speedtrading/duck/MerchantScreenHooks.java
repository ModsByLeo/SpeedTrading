package adudecalledleo.speedtrading.duck;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;

import java.util.List;

public interface MerchantScreenHooks {
    enum State {
        CAN_PERFORM, CLOSED, NO_SELECTION, OUT_OF_STOCK, NOT_ENOUGH_BUY_ITEMS, NO_ROOM_FOR_SELL_ITEM, BLOCKED
    }

    State getState();
    TradeOffer getCurrentTradeOffer();

    void autofillSellSlots();
    void performTrade();
    void clearSellSlots();

    void callRenderTooltip(MatrixStack matrixStack, List<Text> text, int mouseX, int mouseY);
}
