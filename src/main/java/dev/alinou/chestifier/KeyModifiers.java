package dev.alinou.chestifier;

import com.mojang.blaze3d.platform.InputConstants;

public final class KeyModifiers {

    private KeyModifiers() {}

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
            || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(InputConstants.KEY_LALT)
            || InputConstants.isKeyDown(InputConstants.KEY_RALT);
    }
}
