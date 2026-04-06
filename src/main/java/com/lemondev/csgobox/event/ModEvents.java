package com.lemondev.csgobox.event;

import com.lemondev.csgobox.config.CsgoBoxManage;
import com.lemondev.csgobox.item.ItemCsgoBox;
import com.lemondev.csgobox.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Random;

public final class ModEvents {
    private ModEvents() {
    }

    public static void onLivingDeath(LivingEntity mob, DamageSource damageSource) {
        String entityType = Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType())).toString();

        if (CsgoBoxManage.BOX == null) {
            return;
        }

        Random random = new Random();

        for (ItemCsgoBox.BoxInfo info : CsgoBoxManage.BOX) {
            if (info.dropEntity == null || info.dropEntity.isEmpty()) {
                continue;
            }

            if (info.dropRandom > 0 && info.dropRandom > (1.0F - random.nextFloat()) && info.dropEntity.contains(entityType)) {
                ItemStack stack = new ItemStack(ModItems.ITEM_CSGOBOX);
                ItemCsgoBox.setBoxInfo(info, stack);
                if (mob.level() instanceof ServerLevel serverLevel) {
                    mob.spawnAtLocation(serverLevel, stack);
                }
            }
        }
    }
}
