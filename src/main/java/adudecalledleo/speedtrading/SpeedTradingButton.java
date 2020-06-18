package adudecalledleo.speedtrading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;

public class SpeedTradingButton extends AbstractPressableButtonWidget {
    private MerchantScreenAccess msa;

    public SpeedTradingButton(int x, int y, MerchantScreenAccess msa) {
        super(x, y, 18, 20, null);
        this.msa = msa;
    }

    @Override
    public void onPress() {
        while (msa.canPerformTrade())
            msa.performTrade();
    }

    private static final Identifier BUTTON_LOCATIION = new Identifier(SpeedTradingMod.MOD_ID, "textures/gui/speedtrade.png");

    private void updateActiveState() {
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
        updateActiveState();
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
