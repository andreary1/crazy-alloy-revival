package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
 * Shadow Wanderer of the Mystic Grove: a tall, thin, hunched creature in deep purple, nearly black on the front of
 * the torso, with a long head pushed forward that ends in two long jaws around a dark gap, two narrow spikes on top
 * of the head, long arms with big hands of long separate fingers, long bent legs with long feet, and purple and black
 * motes around its legs. Appears at night in the Mystic Grove and is one of the creatures the {@link Troll} hunts.
 * <p>
 * The original does not confirm its attacks, reach or speed; the revival makes it a melee hunter of players with a
 * two-handed claw swipe (action A). Revival animations: a crouched walk with deeply bent knees, swinging arms and
 * twitching fingers, and a recoil (action B) when a heavy blow lands, such as a troll's smash.
 */
public class ShadowWanderer extends Monster implements AnimatedMob {
    /** Damage from one hit that makes it recoil. */
    public static final float RECOIL_DAMAGE = 5.0F;
    private static final DustParticleOptions SHADOW_MOTE = new DustParticleOptions(0x5a2a8a, 1.0F);
    private static final DustParticleOptions DARK_MOTE = new DustParticleOptions(0x1a0f24, 1.2F);

    private final AnimationState clawAnimation = new AnimationState();
    private final AnimationState recoilAnimation = new AnimationState();
    private float stance, stanceO;

    public ShadowWanderer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    public static boolean checkSpawnRules(EntityType<ShadowWanderer> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && nightOrNotNatural(level, reason)
                && SpawnRules.chance(reason, random, RevivalConfig.SHADOW_WANDERER_SPAWN_CHANCE::get);
    }

    /** Natural and chunk-generation spawns of the shadow creatures need a dark sky; eggs and spawners do not. */
    public static boolean nightOrNotNatural(ServerLevelAccessor level, EntitySpawnReason reason) {
        return (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION) || level.getLevel().isDarkOutside();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        level.broadcastEntityEvent(this, EVENT_ACTION_A);
        boolean hit = super.doHurtTarget(level, target);
        if (hit) {
            this.playSound(SoundEvents.PHANTOM_BITE, 0.7F, 0.7F);
        }
        return hit;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && damage >= RECOIL_DAMAGE && this.isAlive()) {
            level.broadcastEntityEvent(this, EVENT_ACTION_B);
        }
        return hurt;
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
    public void aiStep() {
        super.aiStep();
        // Purple and black motes around the legs and feet, a few higher up.
        if (this.level().isClientSide() && this.random.nextInt(3) == 0) {
            double y = this.random.nextInt(4) == 0 ? this.getY(0.4 + this.random.nextDouble() * 0.3) : this.getY() + this.random.nextDouble() * 0.8;
            this.level().addParticle(this.random.nextBoolean() ? SHADOW_MOTE : DARK_MOTE, this.getRandomX(0.8), y, this.getRandomZ(0.8), 0.0, 0.01, 0.0);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.clawAnimation.start(this.tickCount);
        } else if (id == EVENT_ACTION_B) {
            this.recoilAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Two-handed claw swipe. */
    @Override
    public AnimationState actionA() {
        return this.clawAnimation;
    }

    /** Recoil from a heavy blow. */
    @Override
    public AnimationState actionB() {
        return this.recoilAnimation;
    }

    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.stanceO, this.stance);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SHADOW_WANDERER.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SHADOW_WANDERER.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SHADOW_WANDERER.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.3F, 0.6F);
    }
}
