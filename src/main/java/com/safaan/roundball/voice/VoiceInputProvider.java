package com.safaan.roundball.voice;

/** Provider abstraction. The core mod never assumes a particular speech-recognition service. */
public interface VoiceInputProvider extends AutoCloseable {
    boolean isAvailable();
    void startListening(Listener listener);
    void stopListening();
    interface Listener { void onTranscript(String text); void onError(String message); }
    @Override default void close() { stopListening(); }
}
