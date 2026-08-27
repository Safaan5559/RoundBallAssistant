package com.safaan.roundball.client;

import com.safaan.roundball.RoundBallAssistant;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Client entry point and Shift+K interaction foundation. */
public final class RoundBallAssistantClient implements ClientModInitializer {
    private static KeyBinding openAssistant;

    @Override
    public void onInitializeClient() {
        openAssistant = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.roundballassistant.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.roundballassistant"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openAssistant.wasPressed()) {
                if (client.player != null && hasShiftDown()) {
                    client.setScreen(new AssistantScreen());
                }
            }
        });
        RoundBallAssistant.LOGGER.info("Round Ball Assistant client initialized");
    }

    private static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(
                net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle(),
                GLFW.GLFW_KEY_LEFT_SHIFT
        ) || InputUtil.isKeyPressed(
                net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle(),
                GLFW.GLFW_KEY_RIGHT_SHIFT
        );
    }
}
