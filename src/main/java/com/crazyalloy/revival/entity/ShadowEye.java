package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Shadow Eye of the Mystic Grove: a purple, roughly cubic core floating above the ground, with one big orange eye with
 * a vertical pupil and thin dark purple rings around it at different tilts, trailing purple and near-black motes.
 * Appears at night in the Mystic Grove and is one of the creatures the {@link Troll} hunts.
 * <p>
 * The original only shows its look; it gives it no confirmed attack. Revival proposal (config
 * {@code shadowEyeGaze}): a telegraphed shadow gaze. When it has a target in sight it stops, the rings line up and
 * the pupil widens for {@link #CHARGE_TICKS} ticks (action A), then, if the target is still in sight, a beam of dark
 * motes hits it for magic damage and a short Darkness (players) or Slowness (creatures). Breaking line of sight
 * during the warning dodges it. Without the gaze it only floats around and drifts after its target.
 */
public class ShadowEye extends Monster implements AnimatedMob {
    public static final int CHARGE_TICKS = 30;
    public static final float GAZE_DAMAGE = 4.0F;
    public static final double GAZE_RANGE = 16.0;
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(ShadowEye.class, EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions SHADOW_MOTE = new DustParticleOptions(0x5a2a8a, 1.0F);
    private static final DustParticleOptions DARK_MOTE = new DustParticleOptions(0x1a0f24, 1.2F);

    private final AnimationState chargeAnimation = new AnimationState();
    private int gazeCooldown;
    private float charge, chargeO;

    public ShadowEye(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.xpReward = 7;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    /** Night only (the sky must be dark), on top of the usual monster light rules. */
    public static boolean checkSpawnRules(EntityType<ShadowEye> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && ShadowWanderer.nightOrNotNatural(level, reason)
                && SpawnRules.chance(reason, random, RevivalConfig.SHADOW_EYE_SPAWN_CHANCE::get);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CHARGING, false);
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GazeGoal());
        this.goalSelector.addGoal(3, new KeepWatchGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return Fairy.flyingNavigation(this, level);
    }

    @Override
    public void travel(Vec3 input) {
        this.travelFlying(input, this.getSpeed());
    }

    @Override
    protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    /** Ground distance below the core (up to 6 blocks), used to keep it floating at about head height. */
    private double heightAboveGround() {
        BlockPos.MutableBlockPos p = this.blockPosition().mutable();
        for (int i = 0; i < 6; i++) {
            p.move(0, -1, 0);
            if (!this.level().getBlockState(p).getCollisionShape(this.level(), p).isEmpty()) {
                return this.getY() - (p.getY() + 1);
            }
        }
        return 6.0;
    }

    /** Stops, lines up its rings and widens its pupil, then strikes whatever is still in its sight. */
    private class GazeGoal extends Goal {
        private int ticks;

        GazeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ShadowEye.this.getTarget();
            return RevivalConfig.SHADOW_EYE_GAZE.get() && ShadowEye.this.gazeCooldown <= 0 && target != null && target.isAlive()
                    && ShadowEye.this.distanceToSqr(target) < GAZE_RANGE * GAZE_RANGE && ShadowEye.this.getSensing().hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks > 0 && ShadowEye.this.getTarget() != null && ShadowEye.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.ticks = CHARGE_TICKS;
            ShadowEye.this.getNavigation().stop();
            ShadowEye.this.entityData.set(DATA_CHARGING, true);
            ShadowEye.this.level().broadcastEntityEvent(ShadowEye.this, EVENT_ACTION_A);
            ShadowEye.this.playSound(SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND, 0.6F, 1.8F);
        }

        @Override
        public void stop() {
            ShadowEye.this.entityData.set(DATA_CHARGING, false);
            ShadowEye.this.gazeCooldown = 60 + ShadowEye.this.random.nextInt(40);
            this.ticks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = ShadowEye.this.getTarget();
            if (target == null) {
                return;
            }
            ShadowEye.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            ShadowEye.this.setDeltaMovement(ShadowEye.this.getDeltaMovement().scale(0.6));
            if (--this.ticks == 0 && ShadowEye.this.level() instanceof ServerLevel level) {
                gaze(level, target);
            }
        }
    }

    /** The strike at the end of the warning; misses if the target broke line of sight or got out of range. */
    public boolean gaze(ServerLevel level, LivingEntity target) {
        if (!this.getSensing().hasLineOfSight(target) || this.distanceToSqr(target) > (GAZE_RANGE + 2) * (GAZE_RANGE + 2)) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 0.6F, 0.6F);
            return false;
        }
        Vec3 from = this.getEyePosition();
        Vec3 to = target.getEyePosition();
        Vec3 step = to.subtract(from);
        int n = Mth.ceil(step.length() * 2.0);
        for (int i = 1; i <= n; i++) {
            Vec3 p = from.add(step.scale(i / (double) n));
            level.sendParticles(i % 2 == 0 ? SHADOW_MOTE : DARK_MOTE, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
        }
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 0.8F, 0.6F);
        boolean hit = target.hurtServer(level, this.damageSources().indirectMagic(this, this), GAZE_DAMAGE);
        if (hit) {
            target.addEffect(target instanceof Player
                    ? new MobEffectInstance(MobEffects.DARKNESS, 60, 0)
                    : new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), this);
        }
        return hit;
    }

    /** Drifts after its target and keeps a watching distance of about 5 to 10 blocks. */
    private class KeepWatchGoal extends Goal {
        KeepWatchGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return ShadowEye.this.getTarget() != null && ShadowEye.this.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = ShadowEye.this.getTarget();
            if (target == null) {
                return;
            }
            ShadowEye.this.getLookControl().setLookAt(target, 20.0F, 20.0F);
            double d = ShadowEye.this.distanceTo(target);
            Vec3 away = ShadowEye.this.position().subtract(target.position()).normalize();
            Vec3 wanted = null;
            if (d > 10.0 || !ShadowEye.this.getSensing().hasLineOfSight(target)) {
                wanted = target.position().add(away.scale(6.0));
            } else if (d < 5.0) {
                wanted = ShadowEye.this.position().add(away.scale(4.0));
            }
            if (wanted != null) {
                ShadowEye.this.getMoveControl().setWantedPosition(wanted.x, target.getY() + 2.0, wanted.z, 1.0);
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.gazeCooldown > 0) {
            this.gazeCooldown--;
        }
        // Float at about head height: sink when too high up, rise when close to the ground.
        double h = heightAboveGround();
        Vec3 v = this.getDeltaMovement();
        if (this.getTarget() == null && h > 2.0) {
            this.setDeltaMovement(v.x, Math.min(v.y, -0.03), v.z);
        } else if (h < 1.0) {
            this.setDeltaMovement(v.x, Math.max(v.y, 0.04), v.z);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level().isClientSide()) {
            this.chargeO = this.charge;
            this.charge = Mth.approach(this.charge, this.isCharging() ? 1.0F : 0.0F, this.isCharging() ? 1.0F / 20.0F : 0.15F);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Purple and near-black motes, mostly drifting down into the gap under the core.
        if (this.level().isClientSide() && this.random.nextInt(this.isCharging() ? 1 : 3) == 0) {
            this.level().addParticle(this.random.nextBoolean() ? SHADOW_MOTE : DARK_MOTE,
                    this.getRandomX(0.6), this.getY() - this.random.nextDouble() * 0.6, this.getRandomZ(0.6), 0.0, -0.03, 0.0);
            if (this.random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5), this.getY(0.4), this.getRandomZ(0.5), 0.0, -0.01, 0.0);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.chargeAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Start of the gaze warning. */
    @Override
    public AnimationState actionA() {
        return this.chargeAnimation;
    }

    /** How far the gaze warning has built up (rings aligned, pupil wide), 0 to 1. */
    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.chargeO, this.charge);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("GazeCooldown", this.gazeCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.gazeCooldown = input.getIntOr("GazeCooldown", 0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SHADOW_EYE.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SHADOW_EYE.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SHADOW_EYE.death().get();
    }
}
