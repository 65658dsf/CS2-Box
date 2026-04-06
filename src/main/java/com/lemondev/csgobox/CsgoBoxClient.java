package com.lemondev.csgobox;

import com.lemondev.csgobox.gui.RecModScreens;
import net.fabricmc.api.ClientModInitializer;

public class CsgoBoxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RecModScreens.clientLoad();
    }
}
