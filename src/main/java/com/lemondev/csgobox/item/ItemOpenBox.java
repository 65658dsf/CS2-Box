package com.lemondev.csgobox.item;

import com.lemondev.csgobox.gui.CsgoBoxCraftMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ItemOpenBox extends Item {
    public ItemOpenBox(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = new SimpleMenuProvider((id, inventory, ignoredPlayer) -> new CsgoBoxCraftMenu(id, inventory), Component.literal("csgo_box_craft"));
            serverPlayer.openMenu(provider);
        }

        return InteractionResult.CONSUME;
    }
}
