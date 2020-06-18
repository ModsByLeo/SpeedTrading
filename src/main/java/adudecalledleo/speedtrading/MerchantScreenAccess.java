package adudecalledleo.speedtrading;

import net.minecraft.village.TradeOffer;

public interface MerchantScreenAccess {
    TradeOffer getCurrentTradeOffer();
    boolean canPerformTrade();
    void performTrade();
}
