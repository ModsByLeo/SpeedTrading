package adudecalledleo.speedtrading.gui;

import adudecalledleo.speedtrading.MerchantScreenAccess;
import adudecalledleo.speedtrading.SpeedTradingAntiFreezeMeasure;
import adudecalledleo.speedtrading.SpeedTradingMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class SpeedTradingButton extends AbstractPressableButtonWidget {
    private final MerchantScreenAccess msa;
    private MerchantScreenAccess.TradeState cachedTradeState;
    private boolean trading;
    private boolean refill;

    public SpeedTradingButton(int x, int y, MerchantScreenAccess msa) {
        super(x, y, 18, 20, "");
        this.msa = msa;
        trading = false;
        refill = false;
        recacheState();
    }

    @Override
    public void onPress() {
        recacheState();
        if (!trading && cachedTradeState == MerchantScreenAccess.TradeState.CAN_PERFORM) {
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

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
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
        int i = isHovered() ? 18 : 0;
        blit(x, y, 0, trading ? 36 : i, 20, 18, 20, 54);
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        if (!isHovered())
            return;
        String key;
        if (trading)
            key = "speedtrading.tooltip.trading";
        else
            key = "speedtrading.tooltip." + cachedTradeState.name().toLowerCase();
        msa.renderTooltip(key, mouseX, mouseY);
    }
}
