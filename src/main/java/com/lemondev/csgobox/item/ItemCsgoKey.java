package com.lemondev.csgobox.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ItemCsgoKey extends Item {
    public ItemCsgoKey(Properties properties) {
        super(properties.rarity(Rarity.COMMON));
    }
}
