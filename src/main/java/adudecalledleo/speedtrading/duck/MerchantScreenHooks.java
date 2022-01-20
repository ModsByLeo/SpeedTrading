package adudecalledleo.speedtrading.duck;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;

public interface MerchantScreenHooks {
    enum State {
        CAN_PERFORM, CLOSED, NO_SELECTION, OUT_OF_STOCK, NOT_ENOUGH_BUY_ITEMS, NO_ROOM_FOR_SELL_ITEM
    }

    State speedtrading$computeState();
    TradeOffer speedtrading$getCurrentTradeOffer();
    boolean speedtrading$isCurrentTradeOfferBlocked();

    void speedtrading$autofillSellSlots();
    void speedtrading$performTrade();
    void speedtrading$clearSellSlots();

    void speedtrading$callRenderTooltip(MatrixStack matrices, List<Text> lines, int x, int y);
}
