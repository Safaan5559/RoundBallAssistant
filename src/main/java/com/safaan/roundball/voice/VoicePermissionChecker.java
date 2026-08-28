package com.safaan.roundball.voice;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

/**
 * Basic cross-platform microphone availability check.
 *
 * Android launcher permission dialogs are controlled by the launcher/app package;
 * a Fabric mod cannot safely assume it can request Android runtime permissions.
 */
public final class VoicePermissionChecker {
    private VoicePermissionChecker() {}

    public static VoicePermission checkMicrophone() {
        try {
            AudioFormat format = new AudioFormat(16_000, 16, 1, true, false);
            if (!AudioSystem.isLineSupported(new javax.sound.sampled.DataLine.Info(TargetDataLine.class, format))) {
                return VoicePermission.UNAVAILABLE;
            }
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(
                    new javax.sound.sampled.DataLine.Info(TargetDataLine.class, format));
            line.close();
            return VoicePermission.GRANTED;
        } catch (SecurityException ex) {
            return VoicePermission.DENIED;
        } catch (Exception ex) {
            return VoicePermission.UNAVAILABLE;
        }
    }
}
