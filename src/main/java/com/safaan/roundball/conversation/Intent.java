package com.safaan.roundball.conversation;

public record Intent(Type type, String argument, int amount) {
    public enum Type { GREETING, HELP, GIVE_ITEM, FOLLOW, FIND, ATTACK, STOP, UNKNOWN }
    public static Intent unknown() { return new Intent(Type.UNKNOWN, "", 0); }
}
