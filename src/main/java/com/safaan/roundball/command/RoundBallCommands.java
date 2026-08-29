package com.safaan.roundball.command;

import com.mojang.brigadier.CommandDispatcher;
import com.safaan.roundball.RoundBallAssistant;
import com.safaan.roundball.entity.ModEntities;
import com.safaan.roundball.entity.RoundBallEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/** Server commands for spawning and managing the companion. */
public final class RoundBallCommands {
    private RoundBallCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("roundball")
                .then(CommandManager.literal("summon").executes(ctx -> summon(ctx.getSource())))
                .then(CommandManager.literal("help").executes(ctx -> {
                    ctx.getSource().sendFeedback(() -> Text.literal("Round Ball: /roundball summon"), false);
                    return 1;
                })));
    }

    private static int summon(ServerCommandSource source) {
        try {
            var player = source.getPlayerOrThrow();
            ServerWorld world = player.getServerWorld();

            RoundBallEntity ball = ModEntities.ROUND_BALL.create(world);
            if (ball == null) {
                source.sendError(Text.literal("Round Ball could not be created."));
                return 0;
            }

            ball.refreshPositionAndAngles(
                    player.getX() + 1.0,
                    player.getY(),
                    player.getZ(),
                    player.getYaw(),
                    0.0f
            );
            ball.setController(player);

            if (!world.spawnEntity(ball)) {
                source.sendError(Text.literal("Round Ball could not be spawned."));
                return 0;
            }

            source.sendFeedback(() -> Text.literal("Round Ball joined you!"), false);
            return 1;
        } catch (Exception exception) {
            RoundBallAssistant.LOGGER.error("Failed to summon Round Ball", exception);
            source.sendError(Text.literal("Round Ball failed to spawn. Check latest.log for the exact error."));
            return 0;
        }
    }
}
