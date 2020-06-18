package adudecalledleo.speedtrading.config;

public class ModConfig {
    public int ticksBetweenActions = 5;

    public enum TradeBlockBehavior {
        DAMAGEABLE, UNSTACKABLE, DISABLED
    }

    public TradeBlockBehavior tradeBlockBehavior = TradeBlockBehavior.DAMAGEABLE;
}
