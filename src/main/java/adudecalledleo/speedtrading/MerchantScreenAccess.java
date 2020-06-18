package adudecalledleo.speedtrading;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;

public interface MerchantScreenAccess {
    enum TradeState {
        CAN_PERFORM, NO_SELECTION, OUT_OF_STOCK, NO_ROOM_FOR_SELL_ITEM, NOT_ENOUGH_BUY_ITEMS
    }

    boolean isClosed();
    TradeOffer getCurrentTradeOffer();
    TradeState getTradeState();
    void performTrade();
    void refillTradeSlots();
    void clearTradeSlots();
    void renderTooltip(MatrixStack matrices, Text text, int mouseX, int mouseY);
}
