package adudecalledleo.speedtrading.mixin;

import java.util.List;

import adudecalledleo.speedtrading.config.ModConfig;
import adudecalledleo.speedtrading.duck.MerchantScreenHooks;
import adudecalledleo.speedtrading.gui.SpeedTradeButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import static adudecalledleo.speedtrading.util.PlayerInventoryUtil.playerCanAcceptStack;
import static adudecalledleo.speedtrading.util.PlayerInventoryUtil.playerCanPerformTrade;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> implements MerchantScreenHooks {
    @SuppressWarnings("FieldMayBeFinal")
    @Shadow private int selectedIndex;

    @Shadow protected abstract void syncRecipeIndex();

    @Unique private PlayerInventory playerInventory;
    @Unique private SpeedTradeButton speedTradeButton;

    public MerchantScreenMixin() {
        super(null, null, null);
        throw new RuntimeException("Mixin constructor called?!");
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void capturePlayerInventory(MerchantScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        this.playerInventory = inventory;
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addSpeedTradeButton(CallbackInfo ci) {
        addDrawableChild(speedTradeButton = new SpeedTradeButton(x + 247, y + 36, this));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderSpeedTradeButtonTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        speedTradeButton.renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public State getState() {
        if (client == null || client.currentScreen != this)
            return State.CLOSED;
        TradeOffer offer = getCurrentTradeOffer();
        if (offer == null)
            return State.NO_SELECTION;
        if (offer.isDisabled())
            return State.OUT_OF_STOCK;
        ItemStack sellItem = offer.getSellItem();
        ModConfig.TradeBlockBehavior tradeBlockBehavior = ModConfig.get().tradeBlockBehavior;
        switch (tradeBlockBehavior) {
        case DAMAGEABLE:
            if (sellItem.isDamageable())
                return State.BLOCKED;
            break;
        case UNSTACKABLE:
            if (!sellItem.isStackable())
                return State.BLOCKED;
        default:
            break;
        }
        if (!playerCanAcceptStack(playerInventory, sellItem))
            return State.NO_ROOM_FOR_SELL_ITEM;
        if (handler.getSlot(2).hasStack() || playerCanPerformTrade(playerInventory, offer))
            return State.CAN_PERFORM;
        return State.NOT_ENOUGH_BUY_ITEMS;
    }

    @Override
    public TradeOffer getCurrentTradeOffer() {
        TradeOfferList tradeOffers = handler.getRecipes();
        if (selectedIndex < 0 || selectedIndex >= tradeOffers.size())
            return null;
        return tradeOffers.get(selectedIndex);
    }

    @Override
    public void autofillSellSlots() {
        syncRecipeIndex();
    }

    @Override
    public void performTrade() {
        if (getState() == State.CAN_PERFORM)
            onMouseClick(handler.slots.get(2), -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void clearSellSlots() {
        onMouseClick(handler.slots.get(0), -1, 0, SlotActionType.QUICK_MOVE);
        onMouseClick(handler.slots.get(1), -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void callRenderTooltip(MatrixStack matrixStack, List<Text> text, int mouseX, int mouseY) {
        renderTooltip(matrixStack, text, mouseX, mouseY);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        speedTradeButton.tick();
    }
}
