package com.safaan.roundball.action;

import com.safaan.roundball.conversation.Intent;
import com.safaan.roundball.entity.RoundBallEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/** Executes the deterministic in-game actions understood by the assistant. */
public final class ActionManager {
    private ActionManager() {}

    public static ActionResult execute(PlayerEntity player, RoundBallEntity ball, Intent intent) {
        return switch (intent.type()) {
            case GIVE_ITEM -> giveItem(player, intent.argument(), intent.amount());
            case FOLLOW -> { if (ball != null) ball.setController(player); yield ActionResult.ok("Okay, I'll follow you."); }
            case ATTACK -> { if (ball != null) ball.attackNearestHostile(); yield ActionResult.ok("I'll handle the nearby target."); }
            case STOP -> { if (ball != null) ball.getNavigation().stop(); yield ActionResult.ok("Stopped my current movement."); }
            case FIND -> ActionResult.ok("Searching for " + (intent.argument().isBlank() ? "that" : intent.argument()) + ".");
            default -> ActionResult.ok("I'm ready.");
        };
    }

    private static ActionResult giveItem(PlayerEntity player, String name, int amount) {
        if (name == null || name.isBlank()) return ActionResult.fail("Tell me which item you want.");
        Item item = Registries.ITEM.get(Identifier.of("minecraft", name));
        if (item == null) return ActionResult.fail("I don't know that item.");
        ItemStack stack = new ItemStack(item, Math.max(1, Math.min(64, amount)));
        boolean inserted = player.getInventory().insertStack(stack);
        if (!inserted && !stack.isEmpty()) {
            player.dropItem(stack, false);
        }
        return ActionResult.ok("Here is " + name.replace('_', ' ') + ".");
    }
}
