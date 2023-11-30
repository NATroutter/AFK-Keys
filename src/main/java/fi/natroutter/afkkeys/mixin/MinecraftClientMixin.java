package fi.natroutter.afkkeys.mixin;

import fi.natroutter.afkkeys.AFKKeys;
import fi.natroutter.afkkeys.Handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public Screen currentScreen;

    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Final
    public GameOptions options;

    @Shadow @Final
    public Mouse mouse;

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void modifyTick(CallbackInfo info) {
        if (Handler.INSTANCE.isRunningIgnorePause()) {
            if (player == null || !player.isAlive()) {
                Handler.INSTANCE.disable();
            }
            if (currentScreen == null) {
                if (AFKKeys.mouseKey.isPressed()) {
                    mouse.lockCursor();
                } else {
                    mouse.unlockCursor();
                }
            }
        }

        if (Handler.INSTANCE.isRunningIgnorePause()) {
            if (currentScreen == null) {
                if (Handler.INSTANCE.isPaused()) {
                    Handler.INSTANCE.unpause();
                }
            } else {
                if (currentScreen instanceof GameMenuScreen) {
                    options.pauseOnLostFocus = false;
                    MinecraftClient.getInstance().setScreen(null);
                    MinecraftClient.getInstance().mouse.lockCursor();
                    System.out.println("Trigger!!!!");
                }

                if (!Handler.INSTANCE.isPaused()) {
                    Handler.INSTANCE.pause();
                }
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "openGameMenu(Z)V", cancellable = true) // "openPauseMenu(Z)V"
    private void modifyOpenPauseMenu(CallbackInfo info) {
        if (Handler.INSTANCE.isRunning()) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "handleInputEvents()V")
    private void modifyHandleInputEvents(CallbackInfo info) {
        if (AFKKeys.activeKey.wasPressed()) {
            Set<KeyBinding> pressedKeybinds = new HashSet<>();
            for (KeyBinding keyBinding : options.allKeys) {
                if (!keyBinding.isPressed()) continue;

                if (keyBinding != AFKKeys.activeKey) {
                    pressedKeybinds.add(keyBinding);
                }

            }
            if (!pressedKeybinds.isEmpty()) {
                Handler.INSTANCE.enable(pressedKeybinds);
            }
        }

        if (Handler.INSTANCE.wasPaused) {
            Handler.INSTANCE.enabledKeys.forEach(key -> KeyBinding.onKeyPressed(((KeyBindingAccessor) key).getBoundKey()));
            Handler.INSTANCE.wasPaused = false;
        } else {
            Handler.INSTANCE.enabledKeys.forEach(key -> key.setPressed(true));
        }
    }

}