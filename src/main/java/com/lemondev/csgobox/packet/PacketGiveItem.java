package com.lemondev.csgobox.packet;

import com.lemondev.csgobox.CsgoBox;
import com.lemondev.csgobox.item.ItemCsgoBox;
import com.lemondev.csgobox.utils.RandomItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class PacketGiveItem implements CustomPacketPayload {
    public static final Type<PacketGiveItem> TYPE = new Type<>(CsgoBox.id("give_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGiveItem> STREAM_CODEC = CustomPacketPayload.codec(PacketGiveItem::write, PacketGiveItem::new);
    private static final List<ItemStack> ITEM_BUFFER = new ArrayList<>(50);

    private final long seed;

    public PacketGiveItem(long seed) {
        this.seed = seed;
    }

    private PacketGiveItem(FriendlyByteBuf buf) {
        this(buf.readLong());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeLong(seed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketGiveItem payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var box = player.getMainHandItem();

        if (!(box.getItem() instanceof ItemCsgoBox boxItem)) {
            return;
        }

        if (!tryConsumeKeys(player, box)) {
            return;
        }

        var itemList = boxItem.getItemGroup(box);
        var rng = new Random(payload.seed);
        ITEM_BUFFER.clear();

        for (int i = 0; i < 46; i++) {
            int grade = RandomItem.randomItemsGrade(rng, ItemCsgoBox.getRandom(box), player);
            ITEM_BUFFER.add(RandomItem.randomItems(rng, grade, itemList));
        }

        ItemStack giveItem = ITEM_BUFFER.get(45);
        ITEM_BUFFER.clear();

        if (!giveItem.isEmpty()) {
            if (player.getInventory().getFreeSlot() != -1) {
                player.getInventory().add(giveItem);
            } else {
                player.drop(giveItem, true, false);
            }
        }

        box.shrink(1);
    }

    private static boolean tryConsumeKeys(Player entity, ItemStack box) {
        if (entity.hasInfiniteMaterials()) {
            return true;
        }

        return Optional.ofNullable(ItemCsgoBox.getKey(box)).map(key -> {
            for (int i = 0; i < entity.getInventory().getContainerSize(); i++) {
                ItemStack stack = entity.getInventory().getItem(i);

                if (key.equals(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(stack.getItem())).toString())) {
                    stack.shrink(1);
                    return true;
                }
            }

            ItemStack offhand = entity.getOffhandItem();

            if (!offhand.isEmpty() && key.equals(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(offhand.getItem())).toString())) {
                offhand.shrink(1);
                return true;
            }

            return false;
        }).orElse(false);
    }
}
