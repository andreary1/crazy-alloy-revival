package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mushroom Man of the Mushroom Depths: a thin, dark grey humanoid body under a big red mushroom cap with light spots,
 * and two light yellow rectangular eyes that stay visible in the dark (drawn with an emissive layer; they do not light
 * up the blocks around it). Hostile to players; spawns alone in the Mushroom Depths and from the spawners of the
 * Mushroom Outpost and the Mushroom Base.
 * <p>
 * Revival animations: a springy, slightly swaying walk with the cap bobbing and wobbling a beat behind the body, and
 * a two-armed swipe (action A) when it hits.
 */
public class MushroomMan extends Monster implements AnimatedMob {
    private final AnimationState swipeAnimation = new AnimationState();
    private float stance, stanceO;

    public MushroomMan(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random)
                && SpawnRules.chance(reason, random, RevivalConfig.MUSHROOM_MAN_SPAWN_CHANCE::get);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        level.broadcastEntityEvent(this, EVENT_ACTION_A);
        return super.doHurtTarget(level, target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.stanceO = this.stance;
            this.stance = Mth.approach(this.stance, this.isAggressive() ? 1.0F : 0.0F, 0.1F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.swipeAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public AnimationState actionA() {
        return this.swipeAnimation;
    }

    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.stanceO, this.stance);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MUSHROOM_MAN.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MUSHROOM_MAN.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUSHROOM_MAN.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.FUNGUS_STEP, 0.4F, 1.1F);
    }
}
