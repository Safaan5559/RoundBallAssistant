package com.safaan.roundball.voice;

/** Client voice preferences. Credentials must never be committed to Git. */
public final class VoiceSettings {
    private boolean voiceConversations;
    private boolean pushToTalk;
    private int volume = 100;
    private String provider = "online";

    public boolean voiceConversations() { return voiceConversations; }
    public void setVoiceConversations(boolean value) { voiceConversations = value; }

    public boolean pushToTalk() { return pushToTalk; }
    public void setPushToTalk(boolean value) { pushToTalk = value; }

    public int volume() { return volume; }
    public void setVolume(int value) { volume = Math.max(0, Math.min(100, value)); }

    public String provider() { return provider; }
    public void setProvider(String value) { provider = value == null ? "online" : value; }
}
