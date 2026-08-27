package com.safaan.roundball.command;

import com.mojang.brigadier.CommandDispatcher;
import com.safaan.roundball.entity.ModEntities;
import com.safaan.roundball.entity.RoundBallEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
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
        var player = source.getPlayer();
        if (player == null) return 0;
        RoundBallEntity ball = ModEntities.ROUND_BALL.create(player.getWorld());
        if (ball == null) return 0;
        ball.refreshPositionAndAngles(player.getX() + 1.0, player.getY(), player.getZ(), player.getYaw(), 0);
        ball.setController(player);
        player.getWorld().spawnEntity(ball);
        source.sendFeedback(() -> Text.literal("Round Ball joined you!"), false);
        return 1;
    }
}
