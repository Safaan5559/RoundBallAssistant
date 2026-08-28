package com.safaan.roundball.voice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP provider boundary for STT/TTS services.
 *
 * The provider expects an HTTP adapter endpoint supplied by the user/server.
 * Audio capture and playback remain provider-specific so the same core mod can
 * run on desktop launchers and Android-based launchers.
 */
public final class OnlineVoiceProvider implements VoiceInputProvider, VoiceOutputProvider {
    private final OnlineVoiceConfig config;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final HttpClient http = HttpClient.newHttpClient();
    private volatile boolean listening;

    public OnlineVoiceProvider(OnlineVoiceConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public boolean isAvailable() {
        return config.enabled()
                && !config.endpoint().isBlank()
                && !config.apiKey().isBlank();
    }

    @Override
    public void startListening(Listener listener) {
        Objects.requireNonNull(listener);
        if (!isAvailable()) {
            listener.onError("Online voice provider is not configured.");
            return;
        }
        if (listening) return;
        listening = true;
        // Audio capture is supplied by a launcher/platform adapter. This class
        // accepts captured PCM through submitAudio().
    }

    /** Submit a captured audio buffer to the configured STT endpoint. */
    public void submitAudio(byte[] audio, String contentType, Listener listener) {
        if (!listening || !isAvailable() || audio == null || audio.length == 0) return;
        executor.execute(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(config.endpoint() + "/stt"))
                        .header("Authorization", "Bearer " + config.apiKey())
                        .header("Content-Type", contentType == null ? "audio/wav" : contentType)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(audio))
                        .build();
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() / 100 != 2) {
                    listener.onError("STT request failed: HTTP " + response.statusCode());
                    return;
                }
                String transcript = extractText(response.body());
                if (!transcript.isBlank()) listener.onTranscript(transcript);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                listener.onError("Voice request interrupted.");
            } catch (IOException | RuntimeException e) {
                listener.onError("STT request failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void stopListening() {
        listening = false;
    }

    @Override
    public void speak(String text) {
        if (!isAvailable() || text == null || text.isBlank()) return;
        executor.execute(() -> {
            try {
                String json = "{\"text\":\"" + escapeJson(text) + "\"}";
                HttpRequest request = HttpRequest.newBuilder(URI.create(config.endpoint() + "/tts"))
                        .header("Authorization", "Bearer " + config.apiKey())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();
                // The adapter/server is responsible for returning or routing audio
                // to the platform's playback implementation.
                http.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
            } catch (RuntimeException e) {
                // Keep voice failures from crashing Minecraft.
            }
        });
    }

    @Override
    public void stop() { }

    @Override
    public void close() {
        listening = false;
        executor.shutdownNow();
    }

    private static String extractText(String body) {
        if (body == null) return "";
        String trimmed = body.trim();
        if (trimmed.startsWith("{\"text\"")) {
            int colon = trimmed.indexOf(':');
            int first = trimmed.indexOf('"', colon + 1);
            int last = trimmed.lastIndexOf('"');
            if (first >= 0 && last > first) return trimmed.substring(first + 1, last);
        }
        return trimmed;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
