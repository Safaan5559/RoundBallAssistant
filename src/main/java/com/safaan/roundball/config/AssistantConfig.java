package com.safaan.roundball.config;

/** Lightweight client configuration. Persisting this object can be added without changing the voice API. */
public final class AssistantConfig {
    private boolean voiceConversations;
    private boolean pushToTalk;
    private float voiceVolume = 1.0f;
    private int maxCommandLength = 512;

    public boolean voiceConversations() { return voiceConversations; }
    public void setVoiceConversations(boolean value) { voiceConversations = value; }
    public boolean pushToTalk() { return pushToTalk; }
    public void setPushToTalk(boolean value) { pushToTalk = value; }
    public float voiceVolume() { return voiceVolume; }
    public void setVoiceVolume(float value) { voiceVolume = Math.max(0.0f, Math.min(1.0f, value)); }
    public int maxCommandLength() { return maxCommandLength; }
}
