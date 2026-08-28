package com.safaan.roundball.client;

import com.safaan.roundball.config.AssistantConfig;
import com.safaan.roundball.voice.OnlineVoiceConfig;
import com.safaan.roundball.voice.VoiceConfigStore;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Client settings for direct voice conversations and Fish Audio TTS. */
public final class VoiceSettingsScreen extends Screen {
    private final Screen parent;
    private static final AssistantConfig CONFIG = new AssistantConfig();
    private static final OnlineVoiceConfig VOICE = new OnlineVoiceConfig();
    private TextFieldWidget apiKey;
    private TextFieldWidget voiceId;

    static {
        VoiceConfigStore.load(VOICE);
    }

    public VoiceSettingsScreen(Screen parent) {
        super(Text.literal("Round Ball Voice Settings"));
        this.parent = parent;
    }

    public static AssistantConfig config() { return CONFIG; }
    public static OnlineVoiceConfig voiceConfig() { return VOICE; }

    @Override
    protected void init() {
        int w = 320;
        int x = (width - w) / 2;
        int y = Math.max(20, height / 2 - 95);

        addDrawableChild(ButtonWidget.builder(Text.literal("Voice Conversations: " + (CONFIG.voiceConversations() ? "ON" : "OFF")), b -> {
            CONFIG.setVoiceConversations(!CONFIG.voiceConversations());
            b.setMessage(Text.literal("Voice Conversations: " + (CONFIG.voiceConversations() ? "ON" : "OFF")));
        }).dimensions(x, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Push-to-talk: " + (CONFIG.pushToTalk() ? "ON" : "OFF")), b -> {
            CONFIG.setPushToTalk(!CONFIG.pushToTalk());
            b.setMessage(Text.literal("Push-to-talk: " + (CONFIG.pushToTalk() ? "ON" : "OFF")));
        }).dimensions(x, y + 25, w, 20).build());

        apiKey = new TextFieldWidget(textRenderer, x, y + 58, w, 20, Text.literal("Fish Audio API key"));
        apiKey.setMaxLength(512);
        apiKey.setText(VOICE.apiKey());
        apiKey.setRenderTextProvider((text, offset) -> "•".repeat(Math.max(0, text.length() - offset)));
        addDrawableChild(apiKey);

        voiceId = new TextFieldWidget(textRenderer, x, y + 85, w, 20, Text.literal("Verity voice/reference ID"));
        voiceId.setMaxLength(256);
        voiceId.setText(VOICE.voice());
        addDrawableChild(voiceId);

        addDrawableChild(ButtonWidget.builder(Text.literal("Fish Audio TTS: " + (VOICE.enabled() ? "ON" : "OFF")), b -> {
            VOICE.setEnabled(!VOICE.enabled());
            b.setMessage(Text.literal("Fish Audio TTS: " + (VOICE.enabled() ? "ON" : "OFF")));
        }).dimensions(x, y + 112, w, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Done"), b -> {
            VOICE.setApiKey(apiKey.getText().trim());
            VOICE.setVoice(voiceId.getText().trim());
            VoiceConfigStore.save(VOICE);
            close();
        }).dimensions(x, y + 145, w, 20).build());
    }

    @Override
    public void close() { if (client != null) client.setScreen(parent); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, Math.max(8, height / 2 - 125), 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Fish Audio • Verity voice"), width / 2, Math.max(18, height / 2 - 108), 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}
