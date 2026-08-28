package com.safaan.roundball.voice;

import javax.sound.sampled.*;
import java.util.Objects;

/** Desktop microphone capture adapter using Java Sound. */
public final class DesktopVoiceAdapter implements AutoCloseable {
    private static final AudioFormat FORMAT = new AudioFormat(16_000f, 16, 1, true, false);
    private final OnlineVoiceProvider provider;
    private volatile boolean running;
    private TargetDataLine microphone;
    private Thread captureThread;

    public DesktopVoiceAdapter(OnlineVoiceProvider provider) { this.provider = Objects.requireNonNull(provider); }

    public synchronized boolean start(VoiceInputProvider.Listener listener) {
        if (running || !provider.isAvailable()) return false;
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
            if (!AudioSystem.isLineSupported(info)) { listener.onError("Desktop microphone is not supported."); return false; }
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(FORMAT);
            microphone.start();
            running = true;
            provider.startListening(listener);
            captureThread = new Thread(() -> capture(listener), "roundball-microphone");
            captureThread.setDaemon(true);
            captureThread.start();
            return true;
        } catch (LineUnavailableException | SecurityException e) {
            listener.onError("Unable to access the microphone: " + e.getMessage());
            return false;
        }
    }

    private void capture(VoiceInputProvider.Listener listener) {
        byte[] buffer = new byte[4096];
        java.io.ByteArrayOutputStream audio = new java.io.ByteArrayOutputStream();
        try {
            while (running) {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count <= 0) continue;
                audio.write(buffer, 0, count);
                if (audio.size() >= 32_000) {
                    byte[] chunk = audio.toByteArray();
                    audio.reset();
                    provider.submitAudio(chunk, "audio/raw;rate=16000;channels=1", listener);
                }
            }
        } catch (RuntimeException e) {
            if (running) listener.onError("Microphone capture failed: " + e.getMessage());
        } finally { stop(); }
    }

    public synchronized void stop() {
        running = false;
        provider.stopListening();
        if (microphone != null) { microphone.stop(); microphone.close(); microphone = null; }
        captureThread = null;
    }

    @Override public void close() { stop(); }
}
