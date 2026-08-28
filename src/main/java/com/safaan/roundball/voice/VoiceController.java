package com.safaan.roundball.voice;

import java.util.Objects;

/**
 * Coordinates the voice session. Actual online STT/TTS is supplied by a provider.
 */
public final class VoiceController implements AutoCloseable {
    private final VoiceSettings settings;
    private final VoiceInputProvider input;
    private final VoiceOutputProvider output;
    private VoiceSessionState state = VoiceSessionState.DISABLED;

    public VoiceController(VoiceSettings settings, VoiceInputProvider input, VoiceOutputProvider output) {
        this.settings = Objects.requireNonNull(settings);
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
        refreshState();
    }

    public VoiceSessionState state() {
        return state;
    }

    public void refreshState() {
        state = settings.voiceConversations()
                ? (input.isAvailable() ? VoiceSessionState.IDLE : VoiceSessionState.ERROR)
                : VoiceSessionState.DISABLED;
    }

    public void startListening(VoiceInputProvider.Listener listener) {
        if (!settings.voiceConversations()) {
            state = VoiceSessionState.DISABLED;
            return;
        }
        if (!input.isAvailable()) {
            state = VoiceSessionState.ERROR;
            listener.onError("Microphone or online voice provider is unavailable.");
            return;
        }
        state = VoiceSessionState.LISTENING;
        input.startListening(new VoiceInputProvider.Listener() {
            @Override
            public void onTranscript(String transcript) {
                state = VoiceSessionState.PROCESSING;
                listener.onTranscript(transcript);
            }

            @Override
            public void onError(String message) {
                state = VoiceSessionState.ERROR;
                listener.onError(message);
            }
        });
    }

    public void stopListening() {
        input.stopListening();
        if (settings.voiceConversations()) state = VoiceSessionState.IDLE;
    }

    public void speak(String text) {
        if (!settings.voiceConversations()) return;
        state = VoiceSessionState.SPEAKING;
        output.speak(text);
        state = VoiceSessionState.IDLE;
    }

    public void stopSpeaking() {
        output.stop();
        if (settings.voiceConversations()) state = VoiceSessionState.IDLE;
    }

    @Override
    public void close() {
        input.close();
        output.close();
    }
}
