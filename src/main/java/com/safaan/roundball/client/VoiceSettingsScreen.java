package com.safaan.roundball.client;

import com.safaan.roundball.config.AssistantConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Client settings for direct voice conversations. */
public final class VoiceSettingsScreen extends Screen {
    private final Screen parent;
    private static final AssistantConfig CONFIG = new AssistantConfig();

    public VoiceSettingsScreen(Screen parent) {
        super(Text.literal("Round Ball Voice Settings"));
        this.parent = parent;
    }

    public static AssistantConfig config() { return CONFIG; }

    @Override
    protected void init() {
        int w = 300;
        int x = (width - w) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("Voice Conversations: " + (CONFIG.voiceConversations() ? "ON" : "OFF")), b -> {
            CONFIG.setVoiceConversations(!CONFIG.voiceConversations());
            b.setMessage(Text.literal("Voice Conversations: " + (CONFIG.voiceConversations() ? "ON" : "OFF")));
        }).dimensions(x, height / 2 - 35, w, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Push-to-talk: " + (CONFIG.pushToTalk() ? "ON" : "OFF")), b -> {
            CONFIG.setPushToTalk(!CONFIG.pushToTalk());
            b.setMessage(Text.literal("Push-to-talk: " + (CONFIG.pushToTalk() ? "ON" : "OFF")));
        }).dimensions(x, height / 2 - 8, w, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close()).dimensions(x, height / 2 + 35, w, 20).build());
    }

    private void close() { if (client != null) client.setScreen(parent); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 70, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("When enabled, voice mode can use direct speech without opening Shift+K."), width / 2, height / 2 + 65, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}
