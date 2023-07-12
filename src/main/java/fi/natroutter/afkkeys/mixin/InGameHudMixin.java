package fi.natroutter.afkkeys.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.natroutter.afkkeys.Handler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow public abstract TextRenderer getTextRenderer();

    //render HUD when running
    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/gui/DrawContext;F)V")
    private void modifyRender(DrawContext context, float tickDelta, CallbackInfo ci) {

        if (Handler.INSTANCE.isRunning()) {
            TextRenderer renderer = getTextRenderer();
            String[] lines = Handler.INSTANCE.getMessage();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int additive = 0;
            for (int i = lines.length - 1; i >= 0; i--) {

                int width = renderer.getWidth(lines[i]);

                context.drawText(renderer, lines[i], (scaledWidth / 2) - (width / 2), ((scaledHeight / 2) - 20) + additive, 1, true);
                additive -= 10;
            }
            RenderSystem.disableBlend();
        }
    }

}