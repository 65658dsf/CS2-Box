package com.lemondev.csgobox.item;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.lemondev.csgobox.gui.client.CsboxScreen;
import com.lemondev.csgobox.sounds.ModSounds;
import com.lemondev.csgobox.utils.BlurHandler;
import com.lemondev.csgobox.utils.ItemNBT;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemCsgoBox extends Item {
    public static final String BOX_INFO_TAG = "BoxItemInfo";

    public ItemCsgoBox(Properties properties) {
        super(properties.stacksTo(16).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (getBoxInfo(stack) == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            BlurHandler.updateShader(false);
            player.playSound(ModSounds.CS_OPEN, 10F, 1F);
            Minecraft.getInstance().setScreen(new CsboxScreen());
        }

        return InteractionResult.SUCCESS;
    }

    public static int[] getRandom(ItemStack stack) {
        BoxInfo info = getBoxInfo(stack);
        int[] array = {2, 5, 25, 125, 625};

        if (info != null && info.boxRandom != null && info.boxRandom.length > 4) {
            array = info.boxRandom;
        }

        return array;
    }

    public Map<ItemStack, Integer> getItemGroup(ItemStack stack) {
        Map<ItemStack, Integer> itemsMap = new LinkedHashMap<>();
        BoxInfo info = getBoxInfo(stack);

        if (info == null) {
            return itemsMap;
        }

        addItems(itemsMap, info.grade1, 1);
        addItems(itemsMap, info.grade2, 2);
        addItems(itemsMap, info.grade3, 3);
        addItems(itemsMap, info.grade4, 4);
        addItems(itemsMap, info.grade5, 5);
        return itemsMap;
    }

    private static void addItems(Map<ItemStack, Integer> itemsMap, List<String> entries, int grade) {
        if (entries == null) {
            return;
        }

        for (String entry : entries) {
            itemsMap.put(ItemNBT.getStacks(entry), grade);
        }
    }

    public static String getKey(ItemStack stack) {
        BoxInfo info = getBoxInfo(stack);
        return info != null ? info.boxKey : null;
    }

    public static BoxInfo getBoxInfo(ItemStack stack) {
        if (stack.getItem() == ModItems.ITEM_CSGOBOX) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            if (tag.contains(BOX_INFO_TAG)) {
                return tag.getCompound(BOX_INFO_TAG).map(BoxInfo::deserializeNBT).orElse(null);
            }
        }

        return null;
    }

    public static ItemStack setBoxInfo(BoxInfo info, ItemStack stack) {
        if (stack.getItem() == ModItems.ITEM_CSGOBOX) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            CompoundTag infoTag = new CompoundTag();
            BoxInfo.serializeNBT(info, infoTag);
            tag.put(BOX_INFO_TAG, infoTag);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        BoxInfo info = getBoxInfo(stack);

        if (info != null && info.boxName != null) {
            return Component.literal(info.boxName);
        }

        return super.getName(stack);
    }

    public static class BoxInfo {
        @SerializedName("name")
        public String boxName;
        @SerializedName("drop")
        public float dropRandom;
        @SerializedName("key")
        public String boxKey;
        @SerializedName("random")
        public int[] boxRandom;
        @SerializedName("grade1")
        public List<String> grade1 = Lists.newArrayList();
        @SerializedName("grade2")
        public List<String> grade2 = Lists.newArrayList();
        @SerializedName("grade3")
        public List<String> grade3 = Lists.newArrayList();
        @SerializedName("grade4")
        public List<String> grade4 = Lists.newArrayList();
        @SerializedName("grade5")
        public List<String> grade5 = Lists.newArrayList();
        @SerializedName("entity")
        public List<String> dropEntity = Lists.newArrayList();

        public BoxInfo() {
        }

        public BoxInfo(CompoundTag tag) {
            this.boxName = tag.getString("name").orElse("");

            if (tag.contains("key")) {
                this.boxKey = tag.getString("key").orElse("");
            }

            if (tag.contains("drop")) {
                this.dropRandom = tag.getFloat("drop").orElse(0.0F);
            }

            if (tag.contains("random")) {
                this.boxRandom = tag.getIntArray("random").orElse(new int[]{2, 5, 25, 125, 625});
            }

            this.grade1 = readStringList(tag, "grade1");
            this.grade2 = readStringList(tag, "grade2");
            this.grade3 = readStringList(tag, "grade3");
            this.grade4 = readStringList(tag, "grade4");
            this.grade5 = readStringList(tag, "grade5");
            this.dropEntity = readStringList(tag, "entity");
        }

        public static BoxInfo deserializeNBT(CompoundTag tag) {
            return new BoxInfo(tag);
        }

        public static void serializeNBT(BoxInfo info, CompoundTag tag) {
            tag.putString("name", info.boxName);
            tag.putString("key", info.boxKey);
            tag.putFloat("drop", info.dropRandom);
            tag.putIntArray("random", info.boxRandom);
            writeStringList(tag, "grade1", info.grade1);
            writeStringList(tag, "grade2", info.grade2);
            writeStringList(tag, "grade3", info.grade3);
            writeStringList(tag, "grade4", info.grade4);
            writeStringList(tag, "grade5", info.grade5);
            writeStringList(tag, "entity", info.dropEntity);
        }

        private static List<String> readStringList(CompoundTag tag, String key) {
            List<String> values = Lists.newArrayList();

            if (tag.contains(key)) {
                ListTag tagList = tag.getList(key).orElse(new ListTag());
                tagList.forEach(nbt -> {
                    if (nbt instanceof StringTag stringTag) {
                        values.add(stringTag.value());
                    }
                });
            }

            return values;
        }

        private static void writeStringList(CompoundTag tag, String key, List<String> values) {
            if (values == null || values.isEmpty()) {
                return;
            }

            ListTag nbt = new ListTag();
            values.forEach(value -> nbt.add(StringTag.valueOf(value)));
            tag.put(key, nbt);
        }
    }
}
