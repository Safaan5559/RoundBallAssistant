package com.safaan.roundball.voice;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
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
 * Online STT/TTS provider. When provider=fish_audio, TTS is generated directly
 * by Fish Audio using the configured Verity reference voice.
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
        return config.enabled() && !config.apiKey().isBlank()
                && (!config.endpoint().isBlank() || "fish_audio".equalsIgnoreCase(config.provider()));
    }

    @Override
    public void startListening(Listener listener) {
        Objects.requireNonNull(listener);
        if (!isAvailable()) {
            listener.onError("Online voice provider is not configured.");
            return;
        }
        listening = true;
    }

    /** Submit a captured audio buffer to the configured STT endpoint. */
    public void submitAudio(byte[] audio, String contentType, Listener listener) {
        if (!listening || !isAvailable() || audio == null || audio.length == 0) return;
        executor.execute(() -> {
            try {
                URI uri = URI.create(config.endpoint().replaceAll("/$", "") + "/stt");
                HttpRequest request = HttpRequest.newBuilder(uri)
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
    public void stopListening() { listening = false; }

    @Override
    public void speak(String text) {
        if (!isAvailable() || text == null || text.isBlank()) return;
        executor.execute(() -> {
            try {
                if ("fish_audio".equalsIgnoreCase(config.provider())) {
                    speakWithFish(text);
                } else {
                    speakWithGenericEndpoint(text);
                }
            } catch (Exception e) {
                // Voice failures must never crash Minecraft.
            }
        });
    }

    private void speakWithFish(String text) throws IOException, InterruptedException {
        String endpoint = "https://api.fish.audio/v1/tts";
        StringBuilder json = new StringBuilder("{\"text\":\"")
                .append(escapeJson(text)).append("\",\"format\":\"wav\",\"latency\":\"balanced\"");
        if (!config.voice().isBlank()) {
            json.append(",\"reference_id\":\"").append(escapeJson(config.voice())).append("\"");
        }
        json.append('}');

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json")
                .header("model", config.speechModel().isBlank() ? "s2.1-pro-free" : config.speechModel())
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() / 100 != 2) return;
        playWav(response.body());
    }

    private void speakWithGenericEndpoint(String text) throws IOException, InterruptedException {
        String json = "{\"text\":\"" + escapeJson(text) + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.endpoint().replaceAll("/$", "") + "/tts"))
                .header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() / 100 == 2) playWav(response.body());
    }

    /** Plays Fish's WAV response through the desktop audio device. */
    private static void playWav(byte[] wav) throws Exception {
        if (wav == null || wav.length == 0) return;
        try (AudioInputStream input = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wav))) {
            AudioFormat format = input.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(format);
                line.start();
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) line.write(buffer, 0, count);
                line.drain();
                line.stop();
            }
        }
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
