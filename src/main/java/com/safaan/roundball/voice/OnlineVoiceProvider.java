package com.safaan.roundball.voice;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provider boundary for an online STT/TTS service.
 *
 * The core mod never embeds a provider secret. A production adapter should
 * implement the network protocol for the selected provider and return only
 * text/audio results to this class.
 */
public final class OnlineVoiceProvider implements VoiceInputProvider, VoiceOutputProvider {
    private final OnlineVoiceConfig config;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        if (!isAvailable()) {
            listener.onError("Online voice provider is not configured.");
            return;
        }
        // The microphone/STT transport is intentionally delegated to a provider adapter.
        // No audio is transmitted until an enabled provider implements this operation.
        executor.execute(() -> listener.onError("No online STT adapter has been installed."));
    }

    @Override
    public void stopListening() {
        // Provider adapters cancel their active microphone/network request here.
    }

    @Override
    public void speak(String text) {
        if (!isAvailable() || text == null || text.isBlank()) return;
        executor.execute(() -> {
            // Provider adapters submit text to TTS and play the returned audio.
        });
    }

    @Override
    public void stop() {
        // Provider adapters stop active TTS playback here.
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
