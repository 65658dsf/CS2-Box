package com.lemondev.csgobox.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class Networking {
    private Networking() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(PacketGiveItem.TYPE, PacketGiveItem.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGiveItem.TYPE, PacketGiveItem::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
