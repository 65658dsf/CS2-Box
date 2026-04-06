package com.lemondev.csgobox.gui;

import com.mojang.serialization.JsonOps;
import com.lemondev.csgobox.CsgoBox;
import com.lemondev.csgobox.config.CsgoBoxManage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CsgoBoxCraftScreen extends AbstractContainerScreen<CsgoBoxCraftMenu> {
    private static final HashMap<String, Object> guistate = CsgoBoxCraftMenu.guistate;

    private final Level world;
    private final int x;
    private final int y;
    private final int z;
    private final Player entity;
    private EditBox boxName;

    public CsgoBoxCraftScreen(CsgoBoxCraftMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        boxName.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        guiGraphics.blit(Identifier.parse("csgobox:textures/screens/csgo_table.png"), this.leftPos - 4, this.topPos - 38, 0, 0, 512, 512, 512, 512);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    public List<String> itemListExport(Entity entity) {
        List<String> itemName = new ArrayList<>();

        if (entity instanceof Player player && player.containerMenu instanceof Supplier<?> current && current.get() instanceof Map<?, ?> slots) {
            for (int i = 0; i < 35; i++) {
                ItemStack stack = ((Slot) slots.get(i)).getItem();

                if (stack.isEmpty()) {
                    continue;
                }

                ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result().ifPresent(serialized -> itemName.add(serialized.toString()));
            }
        }

        return itemName;
    }

    public List<Integer> gradeListExport(Entity entity) {
        List<Integer> itemGrade = new ArrayList<>();

        if (entity instanceof Player player && player.containerMenu instanceof Supplier<?> current && current.get() instanceof Map<?, ?> slots) {
            int grade = 1;

            for (int i = 0; i < 35; i++) {
                ItemStack stack = ((Slot) slots.get(i)).getItem();

                if (i > 6) {
                    grade = 2;
                }
                if (i > 11) {
                    grade = 3;
                }
                if (i > 14) {
                    grade = 4;
                }
                if (i > 16) {
                    grade = 5;
                }

                if (!BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("minecraft:air")) {
                    itemGrade.add(grade);
                }
            }
        }

        return itemGrade;
    }

    @Override
    public void init() {
        super.init();

        boxName = new EditBox(this.font, this.leftPos + 63, this.topPos + 42, 60, 9, Component.translatable("gui.rec.csgo_box_craft.box_name")) {
            @Override
            public void insertText(String text) {
                super.insertText(text);
                setSuggestion(getValue().isEmpty() ? Component.translatable("gui.csgobox.csgo_box_craft.box_name").getString() : null);
            }
        };

        boxName.setSuggestion(Component.translatable("gui.csgobox.csgo_box_craft.box_name").getString());
        boxName.setMaxLength(32767);
        guistate.put("text:box_name", boxName);
        this.addWidget(boxName);

        Button buttonDown = Button.builder(Component.translatable("gui.csgobox.csgo_box_craft.button_down"), e -> {
            try {
                CsgoBoxManage.updateBoxJson(this.boxName.getValue(), itemListExport(this.entity), gradeListExport(this.entity));
            } catch (IOException exception) {
                CsgoBox.LOGGER.error("Failed to save crafted box config", exception);
            }
        }).bounds(this.leftPos + 146, this.topPos + 31, 35, 20).build();

        guistate.put("button:button_down", buttonDown);
        this.addRenderableWidget(buttonDown);
    }
}
