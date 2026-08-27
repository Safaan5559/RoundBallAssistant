package com.safaan.roundball.voice;

/** Provider abstraction for spoken assistant responses. */
public interface VoiceOutputProvider extends AutoCloseable {
    boolean isAvailable();
    void speak(String text);
    void stop();
    @Override default void close() { stop(); }
}
