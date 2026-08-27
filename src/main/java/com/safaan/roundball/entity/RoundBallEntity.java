package com.safaan.roundball.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/** The assistant companion entity. It is deliberately non-hostile. */
public final class RoundBallEntity extends PathAwareEntity {
    public RoundBallEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setNoGravity(false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.15, 3.0f, 12.0f, false));
        this.goalSelector.add(7, new WanderAroundGoal(this, 0.7));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
    }

    @Override
    protected void initDataTracker(net.minecraft.entity.data.DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }

    @Override
    public boolean shouldDropLoot() { return false; }
}
