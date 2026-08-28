package com.safaan.roundball.voice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persists voice settings in the user's Minecraft config directory, never in the JAR. */
public final class VoiceConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("roundball-voice.json");

    private VoiceConfigStore() { }

    public static void load(OnlineVoiceConfig config) {
        if (!Files.isRegularFile(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;
            if (root.has("enabled")) config.setEnabled(root.get("enabled").getAsBoolean());
            if (root.has("provider")) config.setProvider(root.get("provider").getAsString());
            if (root.has("endpoint")) config.setEndpoint(root.get("endpoint").getAsString());
            if (root.has("apiKey")) config.setApiKey(root.get("apiKey").getAsString());
            if (root.has("speechModel")) config.setSpeechModel(root.get("speechModel").getAsString());
            if (root.has("voice")) config.setVoice(root.get("voice").getAsString());
        } catch (Exception ignored) {
            // Invalid config should not prevent Minecraft from starting.
        }
    }

    public static void save(OnlineVoiceConfig config) {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("enabled", config.enabled());
            root.addProperty("provider", config.provider());
            root.addProperty("endpoint", config.endpoint());
            root.addProperty("apiKey", config.apiKey());
            root.addProperty("speechModel", config.speechModel());
            root.addProperty("voice", config.voice());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(root, writer);
            }
        } catch (Exception ignored) {
            // Saving voice preferences must never crash Minecraft.
        }
    }
}
