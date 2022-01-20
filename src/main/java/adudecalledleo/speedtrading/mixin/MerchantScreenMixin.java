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
import net.minecraft.screen.slot.Slot;
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
    public State speedtrading$computeState() {
        if (client == null || client.currentScreen != this)
            return State.CLOSED;
        TradeOffer offer = speedtrading$getCurrentTradeOffer();
        if (offer == null)
            return State.NO_SELECTION;
        if (offer.isDisabled())
            return State.OUT_OF_STOCK;
        ItemStack sellItem = offer.getSellItem();
        if (!playerCanAcceptStack(playerInventory, sellItem))
            return State.NO_ROOM_FOR_SELL_ITEM;
        if (handler.getSlot(2).hasStack() || playerCanPerformTrade(playerInventory, offer))
            return State.CAN_PERFORM;
        return State.NOT_ENOUGH_BUY_ITEMS;
    }

    @Override
    public TradeOffer speedtrading$getCurrentTradeOffer() {
        TradeOfferList tradeOffers = handler.getRecipes();
        if (selectedIndex < 0 || selectedIndex >= tradeOffers.size())
            return null;
        return tradeOffers.get(selectedIndex);
    }

    @Override
    public boolean speedtrading$isCurrentTradeOfferBlocked() {
        TradeOffer offer = speedtrading$getCurrentTradeOffer();
        if (offer == null)
            return false;
        return ModConfig.get().tradeBlockBehavior.isBlocked(offer.getSellItem());
    }

    @Override
    public void speedtrading$autofillSellSlots() {
        syncRecipeIndex();
    }

    @Override
    public void speedtrading$performTrade() {
        Slot resultSlot = handler.getSlot(2);
        if (!resultSlot.getStack().isEmpty())
            onMouseClick(resultSlot, -1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void speedtrading$clearSellSlots() {
        onMouseClick(null, 0, 0, SlotActionType.QUICK_MOVE);
        onMouseClick(null, 1, 0, SlotActionType.QUICK_MOVE);
    }

    @Override
    public void speedtrading$callRenderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
        renderTooltip(matrices, lines, x, y);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        speedTradeButton.tick();
    }
}
