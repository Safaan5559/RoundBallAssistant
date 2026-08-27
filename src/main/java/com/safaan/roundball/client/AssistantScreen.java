package com.safaan.roundball.client;

import com.safaan.roundball.AssistantService;
import com.safaan.roundball.entity.RoundBallEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Text interface. The same AssistantService is used by the voice architecture. */
public final class AssistantScreen extends Screen {
    private final AssistantService service = new AssistantService();
    private TextFieldWidget input;
    private String lastReply = "Ask me anything about what I can do.";

    public AssistantScreen() { super(Text.literal("Round Ball Assistant")); }

    @Override
    protected void init() {
        int width = Math.min(360, this.width - 30);
        int x = (this.width - width) / 2;
        this.input = new TextFieldWidget(this.textRenderer, x, this.height / 2 - 8, width, 20, Text.literal("Ask the ball"));
        this.input.setMaxLength(512);
        this.addDrawableChild(input);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Send"), button -> submit())
                .dimensions(x, this.height / 2 + 20, width, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Voice settings"), button -> this.client.setScreen(new VoiceSettingsScreen(this)))
                .dimensions(x, this.height / 2 + 46, width, 20).build());
        this.setInitialFocus(input);
    }

    private void submit() {
        if (client == null || client.player == null) return;
        String command = input.getText().trim();
        if (command.isEmpty()) return;
        RoundBallEntity ball = client.world.getEntitiesByClass(RoundBallEntity.class,
                client.player.getBoundingBox().expand(32), e -> true).stream().findFirst().orElse(null);
        service.handle(client.player, ball, command, reply -> lastReply = reply);
        client.player.sendMessage(Text.literal("Round Ball: " + lastReply), false);
        input.setText("");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, width / 2, height / 2 - 50, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(lastReply), width / 2, height / 2 - 30, 0xFFFF55);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}
