package adudecalledleo.speedtrading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SpeedTradingButton extends AbstractPressableButtonWidget {
    private static final LiteralText EMPTY = new LiteralText("");

    private final MerchantScreenAccess msa;
    private MerchantScreenAccess.TradeState cachedTradeState;
    private boolean trading;
    private boolean refill;

    public SpeedTradingButton(int x, int y, MerchantScreenAccess msa) {
        super(x, y, 18, 20, EMPTY);
        this.msa = msa;
        trading = false;
        refill = false;
        recacheState();
    }

    @Override
    public void onPress() {
        recacheState();
        if (cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM) {
            SpeedTradingAntiFreezeMeasure.reset();
            trading = true;
            refill = true;
        }
    }

    private void performTrade() {
        if (trading) {
            if (msa.isClosed()) {
                trading = false;
                return;
            }
            if (SpeedTradingAntiFreezeMeasure.doAction()) {
                if (refill)
                    msa.refillTradeSlots();
                else
                    msa.performTrade();
                refill = !refill;
                recacheState();
                trading = cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM;
                if (!trading) {
                    msa.clearTradeSlots();
                    recacheState();
                }
            }
        }
    }

    private static final Identifier BUTTON_LOCATION = new Identifier(SpeedTradingMod.MOD_ID, "textures/gui/speedtrade.png");

    public void recacheState() {
        cachedTradeState = msa.getTradeState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (msa.isClosed())
            return;
        if (cachedTradeState == MerchantScreenAccess.TradeState.NO_SELECTION)
            recacheState();
        performTrade();
        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(BUTTON_LOCATION);
        RenderSystem.color4f(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, getZOffset(), 0, isHovered() ? 18 : 0, 20, 18, 36, 20);
        if (isHovered())
            renderToolTip(matrices, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        msa.renderTooltip(matrices, new TranslatableText("speedtrading.tooltip." + cachedTradeState.name().toLowerCase()),
                          mouseX, mouseY);
    }
}
