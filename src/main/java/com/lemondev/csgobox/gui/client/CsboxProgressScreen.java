package com.lemondev.csgobox.gui.client;

import com.lemondev.csgobox.item.ItemCsgoBox;
import com.lemondev.csgobox.packet.Networking;
import com.lemondev.csgobox.packet.PacketGiveItem;
import com.lemondev.csgobox.sounds.ModSounds;
import com.lemondev.csgobox.utils.BlurHandler;
import com.lemondev.csgobox.utils.RandomItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CsboxProgressScreen extends Screen {
    private final Level world;
    private final Player entity;
    private final Map<ItemStack, Integer> itemList;
    private final List<ItemStack> itemInput = new ArrayList<>();
    private final List<Integer> gradeInput = new ArrayList<>();
    private final SecureRandom seedBlender = new SecureRandom();
    private final ItemStack boxStack;

    private long seed;
    private int openTime;
    private boolean started;

    public CsboxProgressScreen() {
        super(Component.literal("cs_progress"));

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            this.entity = minecraft.player;
            this.world = entity.level();
            this.boxStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            this.itemList = ((ItemCsgoBox) boxStack.getItem()).getItemGroup(boxStack);
        } else {
            this.entity = null;
            this.world = null;
            this.boxStack = ItemStack.EMPTY;
            this.itemList = Map.of();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, BlurHandler.getBackgroundColor(), 0xFF101010);
        guiGraphics.drawString(this.font, Component.translatable("gui.csgobox.csgo_box.open_box"), this.width / 2 - 40, 24, 0xFFFFFF);

        if (!itemInput.isEmpty()) {
            int index = Math.min(openTime / 3, itemInput.size() - 1);
            ItemStack preview = itemInput.get(index);
            guiGraphics.renderItem(preview, this.width / 2 - 8, this.height / 2 - 20);
            guiGraphics.drawString(this.font, preview.getHoverName(), this.width / 2 - 50, this.height / 2 + 10, 0xD3D3D3);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        openTime++;

        if (!started && entity != null) {
            started = true;
            renderGradeItems();
            Networking.sendToServer(new PacketGiveItem(seed));
            ClientBoxState.set(itemInput.get(45), gradeInput.get(45));
        }

        if (world != null && openTime > 0 && openTime % 6 == 0 && openTime < 90) {
            world.playSound(entity, entity.getX(), entity.getY(), entity.getZ(), ModSounds.CS_DITA, SoundSource.NEUTRAL, 4F, 1F);
        }

        if (openTime >= 90) {
            Minecraft.getInstance().setScreen(new CsLookItemScreen());
        }
    }

    @Override
    public void onClose() {
        BlurHandler.updateShader(true);
        super.onClose();
    }

    private void renderGradeItems() {
        seedBlender.setSeed(System.nanoTime());
        seed = seedBlender.nextLong();
        Random rng = new Random(seed);

        for (int i = 0; i < 50; i++) {
            int grade = RandomItem.randomItemsGrade(rng, ItemCsgoBox.getRandom(boxStack), this.entity);
            gradeInput.add(grade);
            itemInput.add(RandomItem.randomItems(rng, grade, this.itemList));
        }
    }
}
