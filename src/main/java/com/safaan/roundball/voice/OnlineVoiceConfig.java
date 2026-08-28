package com.safaan.roundball.voice;

/** Client-side configuration for an online voice provider. */
public final class OnlineVoiceConfig {
    private boolean enabled;
    private String provider = "fish_audio";
    private String endpoint = "https://api.fish.audio/v1";
    private String apiKey = "";
    private String speechModel = "s2.1-pro-free";
    // Put the Verity Fish Audio reference/model ID here in the client config.
    private String voice = "";
    private boolean sendAudioToProvider;

    public boolean enabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public String provider() { return provider; }
    public void setProvider(String value) { provider = value == null ? "fish_audio" : value; }
    public String endpoint() { return endpoint; }
    public void setEndpoint(String value) { endpoint = value == null ? "" : value; }
    public String apiKey() { return apiKey; }
    public void setApiKey(String value) { apiKey = value == null ? "" : value; }
    public String speechModel() { return speechModel; }
    public void setSpeechModel(String value) { speechModel = value == null ? "s2.1-pro-free" : value; }
    public String voice() { return voice; }
    public void setVoice(String value) { voice = value == null ? "" : value; }
    public boolean sendAudioToProvider() { return sendAudioToProvider; }
    public void setSendAudioToProvider(boolean value) { sendAudioToProvider = value; }
}
