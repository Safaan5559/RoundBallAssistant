package com.safaan.roundball.client;

import com.safaan.roundball.RoundBallAssistant;
import com.safaan.roundball.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Client entry point: GUI, keybinds and custom entity rendering. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class RoundBallAssistantClient implements ClientModInitializer {
    private static KeyBinding openAssistant;

    @Override
    public void onInitializeClient() {
        openAssistant = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.roundballassistant.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,
                "category.roundballassistant"));

        EntityRendererRegistry.register(ModEntities.ROUND_BALL, ctx -> (net.minecraft.client.render.entity.EntityRenderer) new RoundBallEntityRenderer(ctx));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openAssistant.wasPressed()) {
                if (client.player != null && hasShiftDown()) client.setScreen(new AssistantScreen());
            }
        });
        RoundBallAssistant.LOGGER.info("Round Ball Assistant client initialized");
    }

    private static boolean hasShiftDown() {
        long handle = net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
