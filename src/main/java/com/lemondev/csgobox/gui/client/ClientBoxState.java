package com.lemondev.csgobox.gui.client;

import net.minecraft.world.item.ItemStack;

public final class ClientBoxState {
    private static ItemStack openItem = ItemStack.EMPTY;
    private static int grade;

    private ClientBoxState() {
    }

    public static void set(ItemStack itemStack, int itemGrade) {
        openItem = itemStack.copy();
        grade = itemGrade;
    }

    public static ItemStack getOpenItem() {
        return openItem.copy();
    }

    public static int getGrade() {
        return grade;
    }

    public static void clear() {
        openItem = ItemStack.EMPTY;
        grade = 0;
    }
}
