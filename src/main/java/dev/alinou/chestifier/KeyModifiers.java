package dev.alinou.chestifier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public final class KeyModifiers {

    private KeyModifiers() {}

    public static boolean hasShiftDown() {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_SHIFT)
            || InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean hasAltDown() {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_ALT)
            || InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_RIGHT_ALT);
    }
}
