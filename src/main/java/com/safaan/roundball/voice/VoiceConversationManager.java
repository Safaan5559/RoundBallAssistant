package com.safaan.roundball.voice;

import com.safaan.roundball.config.AssistantConfig;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Coordinates direct voice conversations without opening the text assistant screen. */
public final class VoiceConversationManager implements AutoCloseable {
    private final AssistantConfig config;
    private final VoiceInputProvider input;
    private final VoiceOutputProvider output;
    private final Consumer<String> transcriptHandler;
    private final AtomicBoolean listening = new AtomicBoolean();

    public VoiceConversationManager(AssistantConfig config, VoiceInputProvider input,
                                    VoiceOutputProvider output, Consumer<String> transcriptHandler) {
        this.config = Objects.requireNonNull(config);
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
        this.transcriptHandler = Objects.requireNonNull(transcriptHandler);
    }

    public boolean isAvailable() { return input.isAvailable() && output.isAvailable(); }
    public boolean isListening() { return listening.get(); }

    public void start() {
        if (!config.voiceConversations() || !isAvailable() || !listening.compareAndSet(false, true)) return;
        input.startListening(new VoiceInputProvider.Listener() {
            @Override public void onTranscript(String text) {
                if (text != null && !text.isBlank()) transcriptHandler.accept(text.trim());
            }
            @Override public void onError(String message) { listening.set(false); }
        });
    }

    public void stop() {
        if (listening.compareAndSet(true, false)) input.stopListening();
    }

    public void speak(String response) {
        if (config.voiceConversations() && output.isAvailable() && response != null && !response.isBlank()) output.speak(response);
    }

    @Override public void close() { stop(); input.close(); output.close(); }
}
