package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Ent (stage 8): a big living tree of the Mystic Grove, dark wood with blue-green vertical streaks like the mystic logs,
 * a long torso, heavy angular arms, two trunk-like legs, a crown of layered leaves and narrow amber eyes. Spawns alone
 * in the Mystic Grove, wanders slowly and fights back when attacked (it does not hunt anything by itself).
 * <p>
 * Revival additions: slow steps with the torso leaning into them and swaying foliage; a slow two-armed sweep as its
 * attack. Revival proposal ({@link RevivalConfig#ENT_ROOTING}): an idle Ent sometimes takes root, standing still with
 * its arms raised like branches until something hits it or a while passes, so it blends in with the trees around it.
 * Naturally spawned Ents often start rooted.
 */
public class Ent extends PathfinderMob implements AnimatedMob {
    private static final EntityDataAccessor<Boolean> DATA_ROOTED = SynchedEntityData.defineId(Ent.class, EntityDataSerializers.BOOLEAN);

    private final AnimationState sweepAnimation = new AnimationState();
    private int rootTicks;
    private float rootPose, rootPoseO;

    public Ent(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    public static boolean checkSpawnRules(EntityType<Ent> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isSolid() && SpawnRules.chance(reason, random, RevivalConfig.ENT_SPAWN_CHANCE::get);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ROOTED, false);
    }

    public boolean isRooted() {
        return this.entityData.get(DATA_ROOTED);
    }

    /** Takes root for {@code ticks} ticks (0 uproots it). */
    public void setRooted(int ticks) {
        this.rootTicks = ticks;
        this.entityData.set(DATA_ROOTED, ticks > 0);
        if (ticks > 0) {
            this.getNavigation().stop();
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData data) {
        if (reason == EntitySpawnReason.NATURAL || reason == EntitySpawnReason.CHUNK_GENERATION) {
            if (RevivalConfig.ENT_ROOTING.get() && level.getRandom().nextBoolean()) {
                setRooted(600 + level.getRandom().nextInt(1200));
            }
        }
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RootedGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    /** Holds still (no moving, no looking around) while rooted. */
    private class RootedGoal extends Goal {
        RootedGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return Ent.this.isRooted() && Ent.this.getTarget() == null;
        }

        @Override
        public void start() {
            Ent.this.getNavigation().stop();
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.isRooted()) {
            if (this.getTarget() != null || !RevivalConfig.ENT_ROOTING.get() || --this.rootTicks <= 0) {
                setRooted(0);
            }
        } else if (RevivalConfig.ENT_ROOTING.get() && this.getTarget() == null && this.getNavigation().isDone() && this.random.nextInt(900) == 0) {
            setRooted(400 + this.random.nextInt(1200));
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && this.isRooted()) {
            setRooted(0);
            this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 0.6F);
        }
        return hurt;
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
            this.rootPoseO = this.rootPose;
            this.rootPose = Mth.approach(this.rootPose, this.isRooted() ? 1.0F : 0.0F, 0.04F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.sweepAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** The two-armed sweep. */
    @Override
    public AnimationState actionA() {
        return this.sweepAnimation;
    }

    /** 0 to 1: how far into the rooted, tree-like pose it is. */
    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.rootPoseO, this.rootPose);
    }

    @Override
    public boolean isPushable() {
        return !this.isRooted() && super.isPushable();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("RootTicks", this.rootTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setRooted(input.getIntOr("RootTicks", 0));
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isRooted() ? null : ModSounds.ENT.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ENT.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENT.death().get();
    }

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.WOOD_STEP, 0.9F, 0.5F);
    }
}
