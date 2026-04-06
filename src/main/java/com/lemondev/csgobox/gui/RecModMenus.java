package com.lemondev.csgobox.gui;

import com.lemondev.csgobox.CsgoBox;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class RecModMenus {
    public static final MenuType<CsgoBoxCraftMenu> CSGO_BOX_CRAFT = Registry.register(
            BuiltInRegistries.MENU,
            CsgoBox.id("csgo_box_craft"),
            new MenuType<>(CsgoBoxCraftMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    private RecModMenus() {
    }

    public static void register() {
    }
}
