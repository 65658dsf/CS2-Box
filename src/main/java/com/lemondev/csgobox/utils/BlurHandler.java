package com.lemondev.csgobox.utils;

public final class BlurHandler {
    public static boolean isShaderOn;

    private BlurHandler() {
    }

    public static void updateShader(boolean excluded) {
        isShaderOn = !excluded;
    }

    public static int getBackgroundColor() {
        return (128 << 24) | (90 << 16) | (90 << 8) | 90;
    }
}
