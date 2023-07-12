package fi.natroutter.afkkeys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AFKKeys implements ClientModInitializer {

    public static KeyBinding activeKey;
    public static KeyBinding mouseKey;

    @Override
    public void onInitializeClient() {
        activeKey = new KeyBinding(
                "key.afk-keys.active",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.afk-keys");
        KeyBindingHelper.registerKeyBinding(activeKey);

        mouseKey = new KeyBinding(
                "key.afk-keys.grabmouse",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.afk-keys");
        KeyBindingHelper.registerKeyBinding(mouseKey);

    }

}
