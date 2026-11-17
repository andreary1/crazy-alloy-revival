package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Segway: a riderless two-wheeled scooter that drives itself and rams players. 20 health, 3 damage (the original's
 * numbers). It has no face and no rider: two big wheels, a low platform and a tall pole with a T handlebar.
 * Revival addition: the pole tips forward when it speeds up, back when it brakes and sideways into turns, and the
 * wheels spin with its speed; a ram also shoves the target back. It cannot be ridden (a rideable Segway would be a
 * later expansion).
 */
public class Segway extends Monster implements AnimatedMob {
    private final AnimationState unused = new AnimationState();
    private boolean fromBank;
    /** Client: smoothed forward lean (from acceleration) and sideways lean (from turning). */
    private float lean, leanO, roll, rollO;
    private double lastSpeed;
    private float lastYaw;

    public Segway(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, GuardDinosaur.class, Segway.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            Vec3 dir = this.getDeltaMovement().multiply(1.0, 0.0, 1.0);
            if (dir.lengthSqr() < 1.0E-4) {
                dir = target.position().subtract(this.position()).multiply(1.0, 0.0, 1.0);
            }
            dir = dir.normalize();
            living.knockback(0.6, -dir.x, -dir.z);
        }
        return hit;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            Vec3 motion = new Vec3(this.getX() - this.xo, 0.0, this.getZ() - this.zo);
            double speed = motion.length();
            float yaw = this.yBodyRot;
            float turn = Mth.wrapDegrees(yaw - this.lastYaw);
            this.leanO = this.lean;
            this.rollO = this.roll;
            // Speeding up tips the pole forward, braking tips it back; moving at all keeps a slight forward lean.
            float targetLean = (float) Mth.clamp((speed - this.lastSpeed) * 12.0 + speed * 1.2, -0.35, 0.45);
            float targetRoll = Mth.clamp(turn * 0.03F, -0.3F, 0.3F) * (float) Math.min(1.0, speed * 8.0);
            this.lean += (targetLean - this.lean) * 0.25F;
            this.roll += (targetRoll - this.roll) * 0.25F;
            this.lastSpeed = speed;
            this.lastYaw = yaw;
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.tickCount < 5 && this.fromBank && !RevivalConfig.BANK_GUARDS.get()) {
            this.discard();
        }
    }

    @Override
    public AnimationState actionA() {
        return this.unused;
    }

    /** Forward lean of the pole (radians). */
    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.leanO, this.lean);
    }

    @Override
    public float roll(float partialTick) {
        return Mth.lerp(partialTick, this.rollO, this.roll);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("FromBank", this.fromBank);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.fromBank = input.getBooleanOr("FromBank", false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SEGWAY.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SEGWAY.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SEGWAY.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Rolls quietly on its wheels.
    }
}
