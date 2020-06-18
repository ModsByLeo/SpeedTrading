package adudecalledleo.speedtrading.mixin;

import adudecalledleo.speedtrading.MerchantScreenAccess;
import adudecalledleo.speedtrading.SpeedTradingButton;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
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
public abstract class MixinMerchantScreen_AddButton extends HandledScreen<MerchantScreenHandler> implements
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
        addButton(speedtrading$button = new SpeedTradingButton(x + 247, y + 37, this));
        speedtrading$button.updateActiveState();
    }

    @Inject(method = "syncRecipeIndex", at = @At("TAIL"))
    public void speedtrading$updateButton(CallbackInfo ci) {
        speedtrading$button.updateActiveState();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isOpen() {
        return client.currentScreen == this;
    }

    @Override
    public TradeOffer getCurrentTradeOffer() {
        TraderOfferList offers = handler.getRecipes();
        if (selectedIndex < 0 || selectedIndex >= offers.size())
            return null;
        return offers.get(selectedIndex);
    }

    @Override
    public boolean canPerformTrade() {
        TradeOffer offer = getCurrentTradeOffer();
        if (offer.isDisabled() || !playerCanAcceptStack(playerInventory, offer.getMutableSellItem()))
            return false;
        if (handler.slots.get(2).hasStack())
            return true;
        return playerHasStack(playerInventory, offer.getAdjustedFirstBuyItem()) && playerHasStack(playerInventory,
                                                                                                  offer.getSecondBuyItem());
    }

    @Override
    public void performTrade() {
        onMouseClick(handler.slots.get(2), -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void refillTradeSlots() {
        speedtrading$syncRecipeIndex();
    }

    @Override
    public void clearTradeSlots() {
        onMouseClick(handler.slots.get(0), -1, 0, SlotActionType.QUICK_MOVE);
        onMouseClick(handler.slots.get(1), -1, 0, SlotActionType.QUICK_MOVE);
    }
}
