package com.safaan.roundball.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Initial text-command screen. Voice mode will use the same conversation backend without requiring this screen. */
public final class AssistantScreen extends Screen {
    private TextFieldWidget input;

    public AssistantScreen() {
        super(Text.literal("Round Ball Assistant"));
    }

    @Override
    protected void init() {
        int width = 260;
        int x = (this.width - width) / 2;
        this.input = new TextFieldWidget(this.textRenderer, x, this.height / 2 - 10, width, 20, Text.literal("Command"));
        this.input.setMaxLength(512);
        this.addDrawableChild(this.input);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Send"), button -> submit())
                .dimensions(x, this.height / 2 + 18, width, 20)
                .build());
        this.setInitialFocus(this.input);
    }

    private void submit() {
        String command = input.getText().trim();
        if (!command.isEmpty()) {
            // Conversation/action routing is deliberately isolated from the UI.
            // This keeps the same backend usable by text and future voice input.
            this.client.player.sendMessage(Text.literal("Round Ball: I received: " + command), false);
            input.setText("");
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 45, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
