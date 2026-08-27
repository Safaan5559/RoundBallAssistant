package com.safaan.roundball.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

/** A small, persistent assistant ball. Its AI is intentionally non-hostile by default. */
public final class RoundBallEntity extends SlimeEntity {
    private PlayerEntity controller;

    public RoundBallEntity(EntityType<? extends SlimeEntity> type, World world) {
        super(type, world);
        setSize(1, true);
        setPersistent();
    }

    @Override
    protected void initGoals() {
        goalSelector.add(2, new MeleeAttackGoal(this, 1.15, true));
        goalSelector.add(7, new WanderAroundGoal(this, 0.65));
        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    @Override
    protected boolean canAttack() { return false; }

    public void setController(PlayerEntity player) { controller = player; }
    public PlayerEntity getController() { return controller; }

    public void followController() {
        if (controller == null || !controller.isAlive()) return;
        double distance = distanceTo(controller);
        if (distance > 3.0) getNavigation().startMovingTo(controller, distance > 12 ? 1.25 : 1.0);
        getLookControl().lookAt(controller, 30.0f, 30.0f);
    }

    public void requestTarget(LivingEntity target) {
        // The core assistant never chooses a target automatically. A future action provider
        // may explicitly request a target after the player asks for it.
        if (target != null && target.isAlive()) setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient) followController();
    }

    @Override public boolean canImmediatelyDespawn(double distanceSquared) { return false; }
    @Override public boolean shouldDropLoot() { return false; }
}
