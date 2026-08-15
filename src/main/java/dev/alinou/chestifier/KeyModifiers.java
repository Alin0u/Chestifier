package dev.alinou.chestifier;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public final class KeyModifiers {

    private KeyModifiers() {}

    public static boolean hasShiftDown() {
        Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT)
            || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT);
    }

    public static boolean hasAltDown() {
        Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LALT)
            || InputConstants.isKeyDown(window, InputConstants.KEY_RALT);
    }
}
