package com.lemondev.csgobox;

import com.mojang.logging.LogUtils;
import com.lemondev.csgobox.config.CsgoBoxManage;
import com.lemondev.csgobox.event.ModEvents;
import com.lemondev.csgobox.gui.RecModMenus;
import com.lemondev.csgobox.item.ModItems;
import com.lemondev.csgobox.packet.Networking;
import com.lemondev.csgobox.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsgoBox implements ModInitializer {
    public static final String MODID = "csgobox";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModSounds.register();
        RecModMenus.register();
        ModItems.register();
        Networking.register();
        ensureDefaultConfig();

        try {
            CsgoBoxManage.loadConfigBox();
        } catch (IOException exception) {
            LOGGER.error("Failed to load CS2 box configs", exception);
        }

        ServerLivingEntityEvents.AFTER_DEATH.register(ModEvents::onLivingDeath);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    private static void ensureDefaultConfig() {
        Path folderPath = FabricLoader.getInstance().getConfigDir().resolve("csbox");

        try {
            Files.createDirectories(folderPath);

            String content =
                    """
                    {
                      "name": "Weapons Supply Box",
                      "key": "csgobox:csgo_key0",
                      "drop": 0.12,
                      "random": [
                        2,
                        5,
                        6,
                        20,
                        625
                      ],
                      "entity": [
                        "minecraft:zombie",
                        "minecraft:skeleton"
                      ],
                      "grade1": [
                        "{\\"id\\":\\"minecraft:stone_sword\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_shovel\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_pickaxe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_hoe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:iron_sword\\",\\"count\\":1,\\"components\\":{}}"
                      ],
                      "grade2": [
                        "{\\"id\\":\\"minecraft:golden_sword\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:golden_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:golden_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:golden_pickaxe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:golden_shovel\\",\\"count\\":1,\\"components\\":{}}"
                      ],
                      "grade3": [
                        "{\\"id\\":\\"minecraft:diamond_shovel\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:diamond_pickaxe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:diamond_hoe\\",\\"count\\":1,\\"components\\":{}}"
                      ],
                      "grade4": [
                        "{\\"id\\":\\"minecraft:diamond_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:diamond_sword\\",\\"count\\":1,\\"components\\":{}}"
                      ],
                      "grade5": [
                        "{\\"id\\":\\"minecraft:netherite_sword\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:netherite_axe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:netherite_pickaxe\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:netherite_shovel\\",\\"count\\":1,\\"components\\":{}}",
                        "{\\"id\\":\\"minecraft:netherite_hoe\\",\\"count\\":1,\\"components\\":{}}"
                      ]
                    }""";

            Path defaultConfig = folderPath.resolve("default.json");

            if (!Files.exists(defaultConfig)) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(defaultConfig.toFile()), StandardCharsets.UTF_8))) {
                    writer.write(content);
                }
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to create default CS2 box config", exception);
        }
    }
}
