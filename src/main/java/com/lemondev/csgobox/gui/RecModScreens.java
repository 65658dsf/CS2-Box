package com.lemondev.csgobox.gui;

import net.minecraft.client.gui.screens.MenuScreens;

public final class RecModScreens {
    private RecModScreens() {
    }

    public static void clientLoad() {
        MenuScreens.register(RecModMenus.CSGO_BOX_CRAFT, CsgoBoxCraftScreen::new);
    }
}
