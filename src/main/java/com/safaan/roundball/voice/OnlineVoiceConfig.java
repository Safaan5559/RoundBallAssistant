package com.safaan.roundball.voice;

/**
 * Client-side configuration for an online voice provider.
 * API secrets are deliberately not stored in source control or the mod JAR.
 */
public final class OnlineVoiceConfig {
    private boolean enabled;
    private String provider = "custom";
    private String endpoint = "";
    private String apiKey = "";
    private String speechModel = "";
    private String voice = "";
    private boolean sendAudioToProvider;

    public boolean enabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }

    public String provider() { return provider; }
    public void setProvider(String value) { provider = value == null ? "custom" : value; }

    public String endpoint() { return endpoint; }
    public void setEndpoint(String value) { endpoint = value == null ? "" : value; }

    public String apiKey() { return apiKey; }
    public void setApiKey(String value) { apiKey = value == null ? "" : value; }

    public String speechModel() { return speechModel; }
    public void setSpeechModel(String value) { speechModel = value == null ? "" : value; }

    public String voice() { return voice; }
    public void setVoice(String value) { voice = value == null ? "" : value; }

    public boolean sendAudioToProvider() { return sendAudioToProvider; }
    public void setSendAudioToProvider(boolean value) { sendAudioToProvider = value; }
}
