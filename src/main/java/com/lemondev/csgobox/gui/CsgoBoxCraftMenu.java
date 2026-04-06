package com.lemondev.csgobox.gui;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CsgoBoxCraftMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public static final HashMap<String, Object> guistate = new HashMap<>();
    private static final int CUSTOM_SLOT_COUNT = 35;

    public final Level world;
    public final Player entity;
    public int x;
    public int y;
    public int z;
    private final SimpleContainer internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public CsgoBoxCraftMenu(int id, Inventory inv) {
        super(RecModMenus.CSGO_BOX_CRAFT, id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.internal = new SimpleContainer(CUSTOM_SLOT_COUNT);

        int nextId = 0;

        nextId = addCustomRow(nextId, 7, 20, -20);
        nextId = addCustomRow(nextId, 5, 20, -2);
        nextId = addCustomRow(nextId, 3, 20, 16);
        nextId = addCustomRow(nextId, 2, 20, 34);
        nextId = addCustomRow(nextId, 9, 20, 70);
        addCustomRow(nextId, 9, 20, 88);

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inv, column + (row + 1) * 9, 20 + column * 18, 121 + row * 18));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(inv, column, 20 + column * 18, 179));
        }
    }

    private int addCustomRow(int nextId, int count, int startX, int y) {
        for (int i = 0; i < count; i++) {
            int index = nextId++;
            Slot slot = new Slot(internal, index, startX + i * 18, y);
            this.customSlots.put(index, this.addSlot(slot));
        }

        return nextId;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < CUSTOM_SLOT_COUNT) {
                if (!this.moveItemStackTo(itemstack1, CUSTOM_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, CUSTOM_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);

        for (int i = 0; i < internal.getContainerSize(); ++i) {
            ItemStack stack = internal.removeItemNoUpdate(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (!playerIn.getInventory().add(stack)) {
                playerIn.drop(stack, false);
            }
        }
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }
}
