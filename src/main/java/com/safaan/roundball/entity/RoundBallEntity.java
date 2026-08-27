package com.safaan.roundball.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

/** Non-hostile companion that follows the nearest player and can execute assistant tasks. */
public final class RoundBallEntity extends PathAwareEntity {
    private PlayerEntity controller;

    public RoundBallEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setNoGravity(false);
        this.setPersistent();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.add(7, new WanderAroundGoal(this, 0.65));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    public void setController(PlayerEntity player) { this.controller = player; }
    public PlayerEntity getController() { return controller; }

    public void followController() {
        if (controller != null && controller.isAlive()) {
            double distance = this.distanceTo(controller);
            if (distance > 3.0) this.getNavigation().startMovingTo(controller, distance > 12 ? 1.25 : 1.0);
            this.getLookControl().lookAt(controller, 30.0f, 30.0f);
        }
    }

    public void attackNearestHostile() {
        LivingEntity target = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(8),
                e -> e != this && e != controller && e.isAlive() && e instanceof MobEntity && e.getAttacker() != null).stream().findFirst().orElse(null);
        if (target != null) this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient) followController();
    }

    @Override
    protected void initDataTracker(net.minecraft.entity.data.DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override public boolean canImmediatelyDespawn(double distanceSquared) { return false; }
    @Override public boolean shouldDropLoot() { return false; }
}
