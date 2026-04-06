package com.lemondev.csgobox.item;

import com.lemondev.csgobox.CsgoBox;
import com.lemondev.csgobox.config.CsgoBoxManage;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {
    public static final Item ITEM_CSGOBOX = register("csgo_box", new ItemCsgoBox(itemProperties("csgo_box")));
    public static final Item ITEM_CSGO_KEY0 = register("csgo_key0", new ItemCsgoKey(itemProperties("csgo_key0")));
    public static final Item ITEM_CSGO_KEY1 = register("csgo_key1", new ItemCsgoKey(itemProperties("csgo_key1")));
    public static final Item ITEM_CSGO_KEY2 = register("csgo_key2", new ItemCsgoKey(itemProperties("csgo_key2")));
    public static final Item ITEM_CSGO_KEY3 = register("csgo_key3", new ItemCsgoKey(itemProperties("csgo_key3")));
    public static final Item ITEM_CSGO_CRAFT = register("csgo_box_craft", new ItemOpenBox(itemProperties("csgo_box_craft")));

    public static final CreativeModeTab EQUIPMENT_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            CsgoBox.id(CsgoBox.MODID),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup." + CsgoBox.MODID + ".cs_tab"))
                    .icon(() -> new ItemStack(ITEM_CSGO_KEY0))
                    .displayItems((displayParameters, entries) -> {
                        entries.accept(ITEM_CSGO_CRAFT);
                        entries.accept(ITEM_CSGOBOX);
                        entries.accept(ITEM_CSGO_KEY0);
                        entries.accept(ITEM_CSGO_KEY1);
                        entries.accept(ITEM_CSGO_KEY2);
                        entries.accept(ITEM_CSGO_KEY3);

                        for (ItemCsgoBox.BoxInfo info : CsgoBoxManage.BOX) {
                            ItemStack stack = new ItemStack(ITEM_CSGOBOX);
                            ItemCsgoBox.setBoxInfo(info, stack);
                            entries.accept(stack);
                        }
                    })
                    .build()
    );

    private ModItems() {
    }

    public static void register() {
    }

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, CsgoBox.id(name), item);
    }

    private static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, CsgoBox.id(name)));
    }
}
