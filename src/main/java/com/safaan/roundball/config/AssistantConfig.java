package com.safaan.roundball.config;

/** Runtime settings shared by the text and voice interfaces. */
public final class AssistantConfig {
    private boolean voiceConversations;
    private boolean pushToTalk = true;
    private float voiceVolume = 1.0f;
    private int maxCommandLength = 512;

    public boolean voiceConversations() { return voiceConversations; }
    public void setVoiceConversations(boolean value) { voiceConversations = value; }
    public boolean pushToTalk() { return pushToTalk; }
    public void setPushToTalk(boolean value) { pushToTalk = value; }
    public float voiceVolume() { return voiceVolume; }
    public void setVoiceVolume(float value) { voiceVolume = Math.max(0.0f, Math.min(1.0f, value)); }
    public int maxCommandLength() { return maxCommandLength; }
    public void setMaxCommandLength(int value) { maxCommandLength = Math.max(32, Math.min(4096, value)); }
}
