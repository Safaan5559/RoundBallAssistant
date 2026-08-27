package com.safaan.roundball.conversation;

import java.util.function.Consumer;

/** Shared backend for typed and voice conversations. */
public final class ConversationManager {
    private final CommandInterpreter interpreter = new CommandInterpreter();
    private final Consumer<String> responder;

    public ConversationManager(Consumer<String> responder) { this.responder = responder; }

    public Intent receive(String text) {
        Intent intent = interpreter.interpret(text);
        responder.accept(reply(intent));
        return intent;
    }

    public String reply(Intent intent) {
        return switch (intent.type()) {
            case GREETING -> "Hello! I'm ready to help.";
            case HELP -> "I can follow you, find things, give supported items, and react to action requests.";
            case GIVE_ITEM -> intent.argument().isBlank() ? "Tell me which item you want." : "I'll handle the " + intent.argument() + " request.";
            case FOLLOW -> "Okay, I'll follow you.";
            case FIND -> "I'll search for " + (intent.argument().isBlank() ? "it" : intent.argument()) + ".";
            case ATTACK -> "I'll handle that target request.";
            case STOP -> "Okay, stopping my current task.";
            case UNKNOWN -> "I didn't understand that. Try asking me to follow you, find something, or get an item.";
        };
    }
}
