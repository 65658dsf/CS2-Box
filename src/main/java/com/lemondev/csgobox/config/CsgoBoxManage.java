package com.lemondev.csgobox.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemondev.csgobox.CsgoBox;
import com.lemondev.csgobox.item.ItemCsgoBox;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class CsgoBoxManage {
    private static final Gson GSON = new Gson();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("csbox");

    public static final List<ItemCsgoBox.BoxInfo> BOX = Lists.newArrayList();

    public static void loadConfigBox() throws IOException {
        Files.createDirectories(CONFIG_DIR);

        try (var stream = Files.walk(CONFIG_DIR, 1)) {
            boolean[] initialized = {false};

            stream.skip(1)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(boxConfig -> {
                        if (!initialized[0]) {
                            initialized[0] = true;
                            BOX.clear();
                        }

                        try (var reader = Files.newBufferedReader(boxConfig)) {
                            BOX.add(GSON.fromJson(reader, ItemCsgoBox.BoxInfo.class));
                        } catch (Exception exception) {
                            CsgoBox.LOGGER.error("Failed reading {}", boxConfig.getFileName(), exception);
                        }
                    });
        }
    }

    public static void updateBoxJson(String name, List<String> item, List<Integer> grade) throws IOException {
        JsonObject newObject = new JsonObject();
        newObject.addProperty("name", name);
        newObject.addProperty("key", "csgobox:csgo_key0");
        newObject.addProperty("drop", 0);

        JsonArray jsonInt = new JsonArray();
        jsonInt.add(2);
        jsonInt.add(5);
        jsonInt.add(6);
        jsonInt.add(20);
        jsonInt.add(625);
        newObject.add("random", jsonInt);

        JsonArray entityArray = new JsonArray();
        entityArray.add("minecraft:zombie");
        newObject.add("entity", entityArray);

        JsonArray jsonArray1 = new JsonArray();
        JsonArray jsonArray2 = new JsonArray();
        JsonArray jsonArray3 = new JsonArray();
        JsonArray jsonArray4 = new JsonArray();
        JsonArray jsonArray5 = new JsonArray();

        for (int i = 0; i < item.size(); i++) {
            switch (grade.get(i)) {
                case 1 -> jsonArray1.add(item.get(i));
                case 2 -> jsonArray2.add(item.get(i));
                case 3 -> jsonArray3.add(item.get(i));
                case 4 -> jsonArray4.add(item.get(i));
                case 5 -> jsonArray5.add(item.get(i));
                default -> {
                }
            }
        }

        newObject.add("grade1", jsonArray1);
        newObject.add("grade2", jsonArray2);
        newObject.add("grade3", jsonArray3);
        newObject.add("grade4", jsonArray4);
        newObject.add("grade5", jsonArray5);

        writeJsonFile(CONFIG_DIR.resolve(name + ".json"), newObject);
    }

    private static void writeJsonFile(Path filePath, JsonElement jsonElement) {
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, jsonElement.toString(), StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
        } catch (IOException exception) {
            CsgoBox.LOGGER.error("Failed to save box config", exception);
        }
    }
}
