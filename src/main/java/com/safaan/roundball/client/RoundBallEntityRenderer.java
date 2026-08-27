package com.safaan.roundball.client;

import com.safaan.roundball.RoundBallAssistant;
import com.safaan.roundball.entity.RoundBallEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.util.Identifier;

/** Uses Minecraft's slime model with the assistant's generated yellow smile texture. */
public final class RoundBallEntityRenderer extends SlimeEntityRenderer<RoundBallEntity> {
    private static final Identifier TEXTURE = Identifier.of(RoundBallAssistant.MOD_ID, "textures/entity/round_ball.png");

    public RoundBallEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.25f;
    }

    @Override
    public Identifier getTexture(RoundBallEntity entity) {
        return TEXTURE;
    }
}
