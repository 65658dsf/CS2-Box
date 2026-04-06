package com.lemondev.csgobox.gui.client;

import com.lemondev.csgobox.item.ItemCsgoBox;
import com.lemondev.csgobox.utils.BlurHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsboxScreen extends Screen {
    private final Level world;
    private final Player entity;
    private final Map<ItemStack, Integer> itemGroup;
    private final List<ItemStack> itemsList;
    private final List<Integer> gradeList;

    private ItemStack itemKey = ItemStack.EMPTY;
    private ItemStack itemMenu = ItemStack.EMPTY;
    private int boxKeyCount;
    private Component statusMessage = Component.empty();

    public CsboxScreen() {
        super(Component.literal("cs_screen"));

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            this.entity = minecraft.player;
            this.world = entity.level();
            this.itemMenu = entity.getItemInHand(InteractionHand.MAIN_HAND);
            this.itemGroup = ((ItemCsgoBox) itemMenu.getItem()).getItemGroup(itemMenu);
            this.itemsList = itemsListProgress(this.itemGroup);
            this.gradeList = gradeListProgress(this.itemGroup);

            if (ItemCsgoBox.getKey(itemMenu) != null) {
                Identifier identifier = Identifier.parse(ItemCsgoBox.getKey(itemMenu));
                this.itemKey = BuiltInRegistries.ITEM.getOptional(identifier).map(ItemStack::new).orElse(ItemStack.EMPTY);
            }
        } else {
            this.entity = null;
            this.world = null;
            this.itemGroup = Map.of();
            this.itemsList = List.of();
            this.gradeList = List.of();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static List<ItemStack> itemsListProgress(Map<ItemStack, Integer> itemList) {
        List<ItemStack> itemStacks = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            for (Map.Entry<ItemStack, Integer> entry : itemList.entrySet()) {
                if (entry.getValue() == i) {
                    itemStacks.add(entry.getKey());
                }
            }
        }

        return itemStacks;
    }

    private static List<Integer> gradeListProgress(Map<ItemStack, Integer> itemList) {
        List<Integer> grades = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            for (Map.Entry<ItemStack, Integer> entry : itemList.entrySet()) {
                if (entry.getValue() == i) {
                    grades.add(i);
                }
            }
        }

        return grades;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, BlurHandler.getBackgroundColor(), 0xFF202020);
        guiGraphics.drawString(this.font, Component.translatable("gui.csgobox.csgo_box.title"), this.width / 2 - 50, 20, 0xFFFFFF);
        guiGraphics.drawString(this.font, itemMenu.getItem().getName(itemMenu), this.width / 2 - 40, 42, 0xD3D3D3);
        guiGraphics.renderItem(itemMenu, this.width / 2 - 8, 70);

        if (!itemKey.isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.csgobox.csgo_box.label_open"), 24, this.height - 56, 0xD3D3D3);
            guiGraphics.renderItem(itemKey, 24, this.height - 36);
            if (entity != null && entity.hasInfiniteMaterials()) {
                guiGraphics.drawString(this.font, "Creative", 46, this.height - 32, 0x55FF55);
            } else {
                guiGraphics.drawString(this.font, "x" + boxKeyCount, 46, this.height - 32, boxKeyCount > 0 ? 0xFFFFFF : 0xAAAAAA);
            }
        }

        for (int i = 0; i < itemsList.size() && i < 20; i++) {
            int row = i / 10;
            int column = i % 10;
            int x = 20 + column * 18;
            int y = this.height / 2 + row * 22;
            guiGraphics.renderItem(itemsList.get(i), x, y);
            guiGraphics.drawString(this.font, String.valueOf(gradeList.get(i)), x + 1, y + 18, 0xA0A0A0);
        }

        if (!statusMessage.getString().isEmpty()) {
            guiGraphics.drawString(this.font, statusMessage, this.width - 220, this.height - 60, 0xFF8080);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        this.boxKeyCount = getBoxKeyCount();
        this.statusMessage = canOpenBox() ? Component.empty() : Component.literal("Missing key");
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.translatable("gui.csgobox.csgo_box.open_box"), button -> {
            if (entity != null && entity.getMainHandItem().getItem() instanceof ItemCsgoBox && canOpenBox()) {
                ClientBoxState.clear();
                Minecraft.getInstance().setScreen(new CsboxProgressScreen());
            } else {
                statusMessage = Component.literal("Missing key");
            }
        }).bounds(this.width - 190, this.height - 34, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.csgobox.csgo_box.back_box"), button -> onClose()).bounds(this.width - 100, this.height - 34, 80, 20).build());
    }

    @Override
    public void onClose() {
        BlurHandler.updateShader(true);
        super.onClose();
    }

    private int getBoxKeyCount() {
        String requiredKey = ItemCsgoBox.getKey(itemMenu);

        if (entity == null || requiredKey == null || requiredKey.isBlank()) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < entity.getInventory().getContainerSize(); i++) {
            ItemStack stack = entity.getInventory().getItem(i);
            if (Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString().equals(requiredKey)) {
                total += stack.getCount();
            }
        }

        ItemStack offhand = entity.getOffhandItem();

        if (!offhand.isEmpty() && Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(offhand.getItem())).toString().equals(requiredKey)) {
            total += offhand.getCount();
        }

        return total;
    }

    private boolean canOpenBox() {
        String requiredKey = ItemCsgoBox.getKey(itemMenu);

        if (entity == null) {
            return false;
        }

        if (requiredKey == null || requiredKey.isBlank()) {
            return true;
        }

        if (entity.hasInfiniteMaterials()) {
            return true;
        }

        return getBoxKeyCount() > 0;
    }
}
