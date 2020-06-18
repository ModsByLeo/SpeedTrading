package adudecalledleo.speedtrading;

import net.minecraft.village.TradeOffer;

public interface MerchantScreenAccess {
    boolean isOpen();
    TradeOffer getCurrentTradeOffer();
    boolean canPerformTrade();
    void performTrade();
    void refillTradeSlots();
    void clearTradeSlots();
}
