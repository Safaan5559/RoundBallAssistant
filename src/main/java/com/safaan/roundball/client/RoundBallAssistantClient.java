package com.safaan.roundball.client;

import com.safaan.roundball.RoundBallAssistant;
import com.safaan.roundball.entity.ModEntities;
import com.safaan.roundball.voice.AndroidVoiceAdapter;
import com.safaan.roundball.voice.DesktopVoiceAdapter;
import com.safaan.roundball.voice.OnlineVoiceConfig;
import com.safaan.roundball.voice.OnlineVoiceProvider;
import com.safaan.roundball.voice.VoiceInputProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Client entry point: GUI, keybinds, custom entity rendering and cross-platform voice input. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class RoundBallAssistantClient implements ClientModInitializer {
    private static KeyBinding openAssistant;
    private static KeyBinding pushToTalk;
    private static DesktopVoiceAdapter desktopVoice;
    private static AndroidVoiceAdapter androidVoice;
    private static OnlineVoiceProvider voiceProvider;

    @Override
    public void onInitializeClient() {
        openAssistant = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.roundballassistant.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,
                "category.roundballassistant"));
        pushToTalk = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.roundballassistant.push_to_talk", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,
                "category.roundballassistant"));
        EntityRendererRegistry.register(ModEntities.ROUND_BALL, ctx -> (net.minecraft.client.render.entity.EntityRenderer) new RoundBallEntityRenderer(ctx));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openAssistant.wasPressed()) {
                if (client.player != null && hasShiftDown()) client.setScreen(new AssistantScreen());
            }
            if (pushToTalk.isPressed()) {
                ensureVoice();
                if (!voiceRunning()) startVoice(client);
            } else if (voiceRunning()) stopVoice();
        });
        RoundBallAssistant.LOGGER.info("Round Ball Assistant client initialized");
    }

    private static void ensureVoice() {
        if (voiceProvider != null) return;
        OnlineVoiceConfig config = VoiceSettingsScreen.voiceConfig();
        voiceProvider = new OnlineVoiceProvider(config);
        if (isAndroidRuntime()) androidVoice = new AndroidVoiceAdapter(voiceProvider);
        else desktopVoice = new DesktopVoiceAdapter(voiceProvider);
    }

    private static boolean isAndroidRuntime() {
        try { Class.forName("android.os.Build", false, RoundBallAssistantClient.class.getClassLoader()); return true; }
        catch (Throwable ignored) { return false; }
    }

    private static boolean voiceRunning() {
        return desktopVoice != null ? desktopVoice.isRunning() : androidVoice != null && androidVoice.isRunning();
    }

    private static void startVoice(net.minecraft.client.MinecraftClient client) {
        VoiceInputProvider.Listener listener = new VoiceInputProvider.Listener() {
            @Override public void onTranscript(String message) {
                if (client.player != null && message != null && !message.isBlank())
                    client.execute(() -> client.player.sendMessage(net.minecraft.text.Text.literal("[Voice] " + message), false));
            }
            @Override public void onError(String message) {
                if (client.player != null && message != null)
                    client.execute(() -> client.player.sendMessage(net.minecraft.text.Text.literal("[Voice] " + message), false));
            }
        };
        if (desktopVoice != null) desktopVoice.start(listener);
        else if (androidVoice != null) androidVoice.start(listener);
    }

    private static void stopVoice() {
        if (desktopVoice != null) desktopVoice.stop();
        if (androidVoice != null) androidVoice.stop();
    }

    private static boolean hasShiftDown() {
        long handle = net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
