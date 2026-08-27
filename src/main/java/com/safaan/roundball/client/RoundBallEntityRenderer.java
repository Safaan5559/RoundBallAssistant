package com.safaan.roundball.client;

import com.safaan.roundball.RoundBallAssistant;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;

/** Slime renderer reused for the custom assistant ball with a generated yellow face texture. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class RoundBallEntityRenderer extends SlimeEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(RoundBallAssistant.MOD_ID, "textures/entity/round_ball.png");

    public RoundBallEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.25f;
    }

    @Override
    public Identifier getTexture(SlimeEntity entity) { return TEXTURE; }
}
