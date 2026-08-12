package dev.alinou.chestifier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;

public final class KeyModifiers {

    private KeyModifiers() {}

    public static boolean hasShiftDown() {
        Window window = MinecraftClient.getInstance().getWindow();
        return InputUtil.isKeyPressed(window, InputUtil.GLFW_KEY_LEFT_SHIFT)
            || InputUtil.isKeyPressed(window, InputUtil.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean hasAltDown() {
        Window window = MinecraftClient.getInstance().getWindow();
        return InputUtil.isKeyPressed(window, InputUtil.GLFW_KEY_LEFT_ALT)
            || InputUtil.isKeyPressed(window, InputUtil.GLFW_KEY_RIGHT_ALT);
    }
}
