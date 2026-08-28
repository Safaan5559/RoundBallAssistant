package com.safaan.roundball.voice;

/** Detects the runtime audio platform without loading Android classes on desktop. */
public final class VoicePlatform {
    private VoicePlatform() {}

    public static boolean isAndroid() {
        try {
            Class.forName("android.os.Build", false, VoicePlatform.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Android launcher must grant RECORD_AUDIO before starting capture. */
    public static boolean microphonePermissionAvailable() {
        if (!isAndroid()) return true;
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object app = activityThread.getMethod("currentApplication").invoke(null);
            if (app == null) return false;
            Class<?> manifest = Class.forName("android.Manifest$permission");
            String permission = (String) manifest.getField("RECORD_AUDIO").get(null);
            int granted = (Integer) app.getClass().getMethod("checkSelfPermission", String.class).invoke(app, permission);
            return granted == 0;
        } catch (Throwable ignored) {
            // Some launcher runtimes do not expose an Activity/Application to mods.
            // In that case the launcher must handle the permission itself.
            return true;
        }
    }
}
