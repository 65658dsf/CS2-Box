package com.lemondev.csgobox.gui.client;

import com.lemondev.csgobox.sounds.ModSounds;
import com.lemondev.csgobox.utils.BlurHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CsLookItemScreen extends Screen {
    private final Level world;
    private final Player entity;
    private ItemStack openItem = ItemStack.EMPTY;
    private int grade;
    private boolean opened;

    public CsLookItemScreen() {
        super(Component.literal("look_item"));

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            this.world = minecraft.player.level();
            this.entity = minecraft.player;
        } else {
            this.world = null;
            this.entity = null;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, BlurHandler.getBackgroundColor(), 0xFF101010);

        if (!openItem.isEmpty()) {
            guiGraphics.drawString(this.font, openItem.getHoverName(), this.width / 2 - 40, 28, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.translatable("gui.csgobox.csgo_box.grade" + grade), this.width / 2 - 40, 46, 0xD3D3D3);
            guiGraphics.renderItem(openItem, this.width / 2 - 8, this.height / 2 - 8);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();

        if (!opened && entity != null) {
            opened = true;
            openItem = ClientBoxState.getOpenItem();
            grade = ClientBoxState.getGrade();
            entity.playSound(ModSounds.CS_FINSH, 10F, 1F);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.translatable("gui.csgobox.csgo_box.back_box"), button -> onClose()).bounds(this.width / 2 - 40, this.height - 40, 80, 20).build());
    }

    @Override
    public void onClose() {
        BlurHandler.updateShader(true);
        super.onClose();
    }
}
