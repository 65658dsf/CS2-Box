package com.lemondev.csgobox.sounds;

import com.lemondev.csgobox.CsgoBox;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    public static final SoundEvent CS_DITA = register("cs_dita");
    public static final SoundEvent CS_OPEN = register("cs_open");
    public static final SoundEvent CS_FINSH = register("cs_finish");

    private ModSounds() {
    }

    public static void register() {
    }

    private static SoundEvent register(String name) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, CsgoBox.id(name), SoundEvent.createVariableRangeEvent(CsgoBox.id(name)));
    }
}
