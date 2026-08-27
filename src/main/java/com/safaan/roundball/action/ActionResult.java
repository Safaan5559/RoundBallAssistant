package com.safaan.roundball.action;

public record ActionResult(boolean success, String message) {
    public static ActionResult ok(String message) { return new ActionResult(true, message); }
    public static ActionResult fail(String message) { return new ActionResult(false, message); }
}
