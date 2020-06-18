package adudecalledleo.speedtrading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;

public class SpeedTradingButton extends AbstractPressableButtonWidget {
    private final MerchantScreenAccess msa;
    private boolean trading;
    private boolean refill;

    public SpeedTradingButton(int x, int y, MerchantScreenAccess msa) {
        super(x, y, 18, 20, null);
        this.msa = msa;
        trading = false;
        refill = false;
    }

    @Override
    public void onPress() {
        if (msa.canPerformTrade()) {
            SpeedTradingAntiFreezeMeasure.reset();
            trading = true;
            refill = true;
        }
    }

    private void performTrade() {
        if (trading) {
            if (!msa.isOpen()) {
                trading = false;
                return;
            }
            if (SpeedTradingAntiFreezeMeasure.doAction()) {
                if (refill)
                    msa.refillTradeSlots();
                else
                    msa.performTrade();
                refill = !refill;
                trading = msa.canPerformTrade();
                if (!trading) {
                    msa.clearTradeSlots();
                    updateActiveState();
                }
            }
        }
    }

    private static final Identifier BUTTON_LOCATIION = new Identifier(SpeedTradingMod.MOD_ID, "textures/gui/speedtrade.png");

    public void updateActiveState() {
        if (trading)
            return;
        TradeOffer offer = msa.getCurrentTradeOffer();
        if (offer.isDisabled()) {
            active = false;
            return;
        }
        active = msa.canPerformTrade();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        performTrade();
        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(BUTTON_LOCATIION);
        RenderSystem.color4f(1, 1, 1, alpha);
        int i = getYImage(isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, getZOffset(), 0, i * 18, 20, 18, 54, 20);
    }
}
