package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Mushroom Cyclops of the Mushroom Depths: a bulkier humanoid with a nearly black dark blue cap with small light spots,
 * one big square light yellow eye over a dark mouth line, thick segmented arms ending in dark fists and straight,
 * thick legs. Hostile to players. Rarer than the Mushroom Man in the wild; the top of the Mushroom Base has a spawner
 * for it, next to the best chest. Not a boss.
 * <p>
 * Revival proposal (config {@code mushroomCyclopsSlam}): a telegraphed two-fisted slam. When its target is close it
 * stops and raises both fists for {@link #WINDUP_TICKS} ticks (action A, with a creak), then brings them down: anyone
 * still in front of it within reach takes {@link #SLAM_DAMAGE}-ish damage (its attack attribute plus 2) and is knocked
 * back. Stepping out of reach during the wind-up dodges it. Heavy, slow steps; it barely flinches from knockback.
 * Without the slam it hits like a plain melee monster.
 */
public class MushroomCyclops extends Monster implements AnimatedMob {
    public static final int WINDUP_TICKS = 16;
    public static final float SLAM_BONUS = 2.0F;
    public static final double SLAM_REACH = 3.2;
    private static final EntityDataAccessor<Boolean> DATA_WINDUP = SynchedEntityData.defineId(MushroomCyclops.class, EntityDataSerializers.BOOLEAN);

    private final AnimationState slamAnimation = new AnimationState();
    private int slamCooldown;
    private float stance, stanceO;

    public MushroomCyclops(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random)
                && SpawnRules.chance(reason, random, RevivalConfig.MUSHROOM_CYCLOPS_SPAWN_CHANCE::get);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WINDUP, false);
    }

    public boolean isWindingUp() {
        return this.entityData.get(DATA_WINDUP);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SlamGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Walks up to the target; once in reach it stops, winds up (fists raised) and slams. With the slam switched off in
     * the config it swings at once, like a plain melee attack.
     */
    private class SlamGoal extends Goal {
        private int windup = -1;
        private int repath;

        SlamGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MushroomCyclops.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = MushroomCyclops.this.getTarget();
            return (target != null && target.isAlive() && !(target instanceof Player p && (p.isCreative() || p.isSpectator()))) || this.windup >= 0;
        }

        @Override
        public void start() {
            MushroomCyclops.this.setAggressive(true);
            this.windup = -1;
            this.repath = 0;
        }

        @Override
        public void stop() {
            MushroomCyclops.this.setAggressive(false);
            MushroomCyclops.this.entityData.set(DATA_WINDUP, false);
            this.windup = -1;
            MushroomCyclops.this.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            MushroomCyclops self = MushroomCyclops.this;
            LivingEntity target = self.getTarget();
            if (this.windup >= 0) {
                if (target != null) {
                    self.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                if (--this.windup < 0 && self.level() instanceof ServerLevel level) {
                    self.entityData.set(DATA_WINDUP, false);
                    self.slam(level);
                    self.slamCooldown = 30;
                }
                return;
            }
            if (target == null) {
                return;
            }
            self.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double dist = self.distanceToSqr(target);
            boolean inReach = dist <= SLAM_REACH * SLAM_REACH && self.getSensing().hasLineOfSight(target);
            if (inReach && self.slamCooldown <= 0) {
                self.getNavigation().stop();
                if (RevivalConfig.MUSHROOM_CYCLOPS_SLAM.get()) {
                    this.windup = WINDUP_TICKS;
                    self.entityData.set(DATA_WINDUP, true);
                    self.level().broadcastEntityEvent(self, EVENT_ACTION_A);
                    self.playSound(SoundEvents.RAVAGER_ROAR, 0.5F, 1.6F);
                } else if (self.level() instanceof ServerLevel level) {
                    self.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                    self.doHurtTarget(level, target);
                    self.slamCooldown = 20;
                }
            } else if (!inReach && --this.repath <= 0) {
                this.repath = 4 + self.random.nextInt(5);
                self.getNavigation().moveTo(target, 1.0);
            }
        }
    }

    /** The end of the wind-up: hits every player or creature it was after in a short arc in front of it. */
    public int slam(ServerLevel level) {
        Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot());
        Vec3 centre = this.position().add(look.scale(1.4));
        int hits = 0;
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + SLAM_BONUS;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(SLAM_REACH, 1.0, SLAM_REACH),
                e -> e != this && e.isAlive() && (e == this.getTarget() || e instanceof Player))) {
            Vec3 to = e.position().subtract(this.position());
            if (to.horizontalDistance() > SLAM_REACH + e.getBbWidth() / 2.0 || (to.horizontalDistance() > 0.5 && to.normalize().dot(look) < 0.2)) {
                continue;
            }
            if (e instanceof Player p && (p.isCreative() || p.isSpectator())) {
                continue;
            }
            if (e.hurtServer(level, this.damageSources().mobAttack(this), damage)) {
                e.knockback(1.2, -look.x, -look.z);
                e.push(0.0, 0.3, 0.0);
                hits++;
            }
        }
        BlockState ground = level.getBlockState(BlockPos.containing(centre).below());
        if (!ground.isAir()) {
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), centre.x, centre.y + 0.1, centre.z, 24, 0.6, 0.05, 0.6, 0.15);
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.5F, 1.5F);
        this.playSound(SoundEvents.RAVAGER_STEP, 1.0F, 0.6F);
        return hits;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.slamCooldown > 0) {
            this.slamCooldown--;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.stanceO = this.stance;
            this.stance = Mth.approach(this.stance, this.isAggressive() ? 1.0F : 0.0F, 0.08F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.slamAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Wind-up and slam. */
    @Override
    public AnimationState actionA() {
        return this.slamAnimation;
    }

    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.stanceO, this.stance);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SlamCooldown", this.slamCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.slamCooldown = input.getIntOr("SlamCooldown", 0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MUSHROOM_CYCLOPS.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MUSHROOM_CYCLOPS.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUSHROOM_CYCLOPS.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.45F, 1.2F);
    }
}
