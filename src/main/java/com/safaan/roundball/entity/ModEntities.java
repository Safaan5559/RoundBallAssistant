package com.safaan.roundball.entity;

import com.safaan.roundball.RoundBallAssistant;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    private ModEntities() {}

    public static final EntityType<RoundBallEntity> ROUND_BALL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(RoundBallAssistant.MOD_ID, "round_ball"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RoundBallEntity::new)
                    .dimensions(EntityDimensions.fixed(0.65f, 0.65f))
                    .trackRangeBlocks(32)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static void initialize() {
        RoundBallAssistant.LOGGER.info("Registered Round Ball entity");
    }
}
