package adudecalledleo.speedtrading.mixin;

import adudecalledleo.speedtrading.MerchantScreenAccess;
import adudecalledleo.speedtrading.config.ModConfig;
import adudecalledleo.speedtrading.config.ModConfigHolder;
import adudecalledleo.speedtrading.gui.SpeedTradingButton;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SlotActionType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static adudecalledleo.speedtrading.SpeedTradingMod.playerCanAcceptStack;
import static adudecalledleo.speedtrading.SpeedTradingMod.playerHasStack;

@Mixin(MerchantScreen.class)
public abstract class MixinMerchantScreen_AddButton extends ContainerScreen<MerchantContainer> implements
                                                                                                 MerchantScreenAccess {
    public MixinMerchantScreen_AddButton() {
        super(null, null, null);
        throw new RuntimeException("Mixin constructor called???");
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Shadow private int selectedIndex;

    @Shadow(prefix = "speedtrading$")
    protected abstract void speedtrading$syncRecipeIndex();

    private SpeedTradingButton speedtrading$button;

    @Inject(method = "init", at = @At("TAIL"))
    public void speedtrading$initButton(CallbackInfo ci) {
        addButton(speedtrading$button = new SpeedTradingButton(x + 247, y + 36, this));
        speedtrading$syncRecipeIndex();
    }

    @Inject(method = "syncRecipeIndex", at = @At("TAIL"))
    public void speedtrading$updateButton(CallbackInfo ci) {
        if (speedtrading$button != null)
            speedtrading$button.recacheState();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void spedtrading$renderButtonTooltip(int mouseX, int mouseY, float delta,
                                                CallbackInfo ci) {
        if (speedtrading$button != null)
            speedtrading$button.renderToolTip(mouseX, mouseY);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isClosed() {
        return minecraft.currentScreen != this;
    }

    @Override
    public TradeOffer getCurrentTradeOffer() {
        TraderOfferList offers = container.getRecipes();
        if (selectedIndex < 0 || selectedIndex >= offers.size())
            return null;
        return offers.get(selectedIndex);
    }

    @Override
    public TradeState getTradeState() {
        TradeOffer offer = getCurrentTradeOffer();
        if (offer == null)
            return TradeState.NO_SELECTION;
        ItemStack sellItem = offer.getMutableSellItem();
        final ModConfig.TradeBlockBehavior tradeBlockBehavior = ModConfigHolder.getConfig().tradeBlockBehavior;
        if (tradeBlockBehavior == ModConfig.TradeBlockBehavior.DAMAGEABLE && sellItem.isDamageable() ||
            tradeBlockBehavior == ModConfig.TradeBlockBehavior.UNSTACKABLE && !sellItem.isStackable())
            return TradeState.BLOCKED;
        if (container.slots.get(2).hasStack())
            return TradeState.CAN_PERFORM;
        if (offer.isDisabled())
            return TradeState.OUT_OF_STOCK;
        if (!playerCanAcceptStack(playerInventory, sellItem))
            return TradeState.NO_ROOM_FOR_SELL_ITEM;
        if (playerHasStack(playerInventory, offer.getAdjustedFirstBuyItem()) && playerHasStack(playerInventory,
                                                                                               offer.getSecondBuyItem()))
            return TradeState.CAN_PERFORM;
        return TradeState.NOT_ENOUGH_BUY_ITEMS;
    }

    @Override
    public void performTrade() {
        onMouseClick(container.slots.get(2), -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void refillTradeSlots() {
        speedtrading$syncRecipeIndex();
    }

    @Override
    public void clearTradeSlots() {
        onMouseClick(container.slots.get(0), -1, 0, SlotActionType.QUICK_MOVE);
        onMouseClick(container.slots.get(1), -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void renderTooltip(String text, int mouseX, int mouseY) {
        super.renderTooltip(text, mouseX, mouseY);
    }
}
