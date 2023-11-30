package fi.natroutter.afkkeys.mixin;

import fi.natroutter.afkkeys.AFKKeys;
import fi.natroutter.afkkeys.Handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(at = @At("HEAD"), method = "isCursorLocked()Z", cancellable = true) // "isCursorLocked()Z"
    private void modifyIsCursorLocked(CallbackInfoReturnable<Boolean> info) {
        if (MinecraftClient.getInstance().currentScreen == null && Handler.INSTANCE.isRunning()) {
            info.setReturnValue(true);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "lockCursor()V", cancellable = true)
    private void modifyLockCursor(CallbackInfo info) {
        if (Handler.INSTANCE.isRunning() && !AFKKeys.mouseKey.isPressed()) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "onCursorPos(JDD)V", cancellable = true)
    private void modifyOnCursorPos(CallbackInfo info) {
        if (Handler.INSTANCE.isRunning() && !AFKKeys.mouseKey.isPressed()) info.cancel();
    }

}