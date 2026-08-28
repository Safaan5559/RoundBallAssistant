package com.safaan.roundball.voice;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Android microphone adapter using android.media.AudioRecord through reflection.
 * Reflection keeps Android classes out of the desktop compile/runtime path.
 */
public final class AndroidVoiceAdapter implements AutoCloseable {
    private static final int SAMPLE_RATE = 16_000;
    private final OnlineVoiceProvider provider;
    private volatile boolean running;
    private Object recorder;
    private Thread captureThread;

    public AndroidVoiceAdapter(OnlineVoiceProvider provider) { this.provider = Objects.requireNonNull(provider); }
    public boolean isRunning() { return running; }

    public synchronized boolean start(VoiceInputProvider.Listener listener) {
        if (running || !provider.isAvailable()) return false;
        try {
            Class<?> audioRecord = Class.forName("android.media.AudioRecord");
            Class<?> audioFormat = Class.forName("android.media.AudioFormat");
            Class<?> audioSource = Class.forName("android.media.MediaRecorder$AudioSource");
            int mic = audioSource.getField("MIC").getInt(null);
            int mono = audioFormat.getField("CHANNEL_IN_MONO").getInt(null);
            int pcm16 = audioFormat.getField("ENCODING_PCM_16BIT").getInt(null);
            Method minBuffer = audioRecord.getMethod("getMinBufferSize", int.class, int.class, int.class);
            int min = (Integer) minBuffer.invoke(null, SAMPLE_RATE, mono, pcm16);
            int bufferSize = Math.max(min * 2, 8192);
            Constructor<?> ctor = audioRecord.getConstructor(int.class, int.class, int.class, int.class, int.class);
            recorder = ctor.newInstance(mic, SAMPLE_RATE, mono, pcm16, bufferSize);
            Method start = audioRecord.getMethod("startRecording");
            Method read = audioRecord.getMethod("read", byte[].class, int.class, int.class);
            start.invoke(recorder);
            running = true;
            provider.startListening(listener);
            captureThread = new Thread(() -> capture(read, bufferSize, listener), "roundball-android-microphone");
            captureThread.setDaemon(true);
            captureThread.start();
            return true;
        } catch (Throwable e) {
            running = false;
            recorder = null;
            listener.onError("Unable to access Android microphone. Grant microphone permission to the launcher.");
            return false;
        }
    }

    private void capture(Method read, int bufferSize, VoiceInputProvider.Listener listener) {
        byte[] buffer = new byte[Math.max(4096, bufferSize / 2)];
        java.io.ByteArrayOutputStream audio = new java.io.ByteArrayOutputStream();
        try {
            while (running && recorder != null) {
                int count = (Integer) read.invoke(recorder, buffer, 0, buffer.length);
                if (count <= 0) continue;
                audio.write(buffer, 0, count);
                if (audio.size() >= 32_000) {
                    byte[] chunk = audio.toByteArray();
                    audio.reset();
                    provider.submitAudio(chunk, "audio/raw;rate=16000;channels=1", listener);
                }
            }
        } catch (Throwable e) {
            if (running) listener.onError("Android microphone capture failed: " + e.getMessage());
        } finally { stop(); }
    }

    public synchronized void stop() {
        running = false;
        provider.stopListening();
        if (recorder != null) {
            try { recorder.getClass().getMethod("stop").invoke(recorder); } catch (Throwable ignored) { }
            try { recorder.getClass().getMethod("release").invoke(recorder); } catch (Throwable ignored) { }
            recorder = null;
        }
        captureThread = null;
    }

    @Override public void close() { stop(); }
}
