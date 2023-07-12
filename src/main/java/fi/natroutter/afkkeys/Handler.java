package fi.natroutter.afkkeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.BOLD;

public class Handler {

    public static final Handler INSTANCE = new Handler();

    private boolean running = false;
    private boolean paused = false;
    public boolean wasPaused = false;

    public Set<KeyBinding> enabledKeys = new HashSet<>();

    public boolean isRunning() {
        return running && !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunningIgnorePause() {
        return running;
    }

    public void enable(Set<KeyBinding> keys) {
        running = true;
        enabledKeys.addAll(keys);
        MinecraftClient.getInstance().mouse.unlockCursor();
    }

    public String[] getMessage() {

        String[] msg = new String[2];

        if (MinecraftClient.getInstance().player != null) {
            StringBuilder str = new StringBuilder();
            str.append(RED).append("Held down ");

            for (int i = 0; i < enabledKeys.size(); i++) {
                KeyBinding keyBinding = enabledKeys.toArray(new KeyBinding[0])[i];
                String keyName = keyBinding.getBoundKeyLocalizedText().getString().toUpperCase();
                int size = enabledKeys.size();

                if (size == 1 || i == size - 2) {
                    str.append(DARK_RED).append(BOLD).append(keyName);
                } else if (i == size - 1) {
                    str.append(RED).append(" and ").append(DARK_RED).append(BOLD).append(keyName);
                } else {
                    str.append(DARK_RED).append(BOLD).append(keyName).append(RED).append(", ");
                }
            }

            msg[0] = str.toString();
            msg[1] = (RED + "Press " + DARK_RED + BOLD + "ESCAPE" + RED + " to exit");
            return msg;
        }
        return new String[0];
    }

    public void disable() {
        enabledKeys.forEach(key -> key.setPressed(false));
        enabledKeys.clear();
        running = false;
        unpause();
        wasPaused = false;
        if (MinecraftClient.getInstance().currentScreen == null) MinecraftClient.getInstance().mouse.lockCursor();
    }

    public void pause() {
        paused = true;
        wasPaused = true;
    }

    public void unpause() {
        paused = false;
    }
}