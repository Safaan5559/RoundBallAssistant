package com.safaan.roundball;

import com.safaan.roundball.action.ActionManager;
import com.safaan.roundball.action.ActionResult;
import com.safaan.roundball.conversation.CommandInterpreter;
import com.safaan.roundball.conversation.Intent;
import com.safaan.roundball.entity.RoundBallEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

/** Single service used by both typed commands and future voice transcripts. */
public final class AssistantService {
    private final CommandInterpreter interpreter = new CommandInterpreter();

    public ActionResult handle(PlayerEntity player, RoundBallEntity ball, String input, Consumer<String> response) {
        Intent intent = interpreter.interpret(input);
        ActionResult result = ActionManager.execute(player, ball, intent);
        response.accept(result.message());
        return result;
    }
}
