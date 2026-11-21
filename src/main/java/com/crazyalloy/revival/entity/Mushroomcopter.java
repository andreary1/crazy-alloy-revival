package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.entity.projectile.SporeShot;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Mushroomcopter of the Mushroom Depths: a compact flier with a wide, roughly cubic purple cap with big irregular light
 * spots, a small dark grey face with light, slanted eyes and a horizontal propeller on a short rod above the cap. No arms
 * or legs. Hostile to players and attacks from a distance with its own projectile, the {@link SporeShot}.
 * <p>
 * Revival animations and sounds: the propeller spins all the time and a low whir plays every couple of seconds, so
 * you hear it coming; before each shot the propeller speeds up and the body tips forward for {@link #AIM_TICKS} ticks
 * (action A), which tells you a spore is coming. It hovers about three blocks up and keeps 6 to 12 blocks away from
 * its target.
 */
public class Mushroomcopter extends Monster implements AnimatedMob {
    public static final int AIM_TICKS = 12;
    public static final double SHOOT_RANGE = 16.0;
    private static final EntityDataAccessor<Boolean> DATA_AIMING = SynchedEntityData.defineId(Mushroomcopter.class, EntityDataSerializers.BOOLEAN);

    private final AnimationState aimAnimation = new AnimationState();
    private int shotCooldown;
    private float aim, aimO;
    private float spin, spinO;

    public Mushroomcopter(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.xpReward = 7;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random)
                && SpawnRules.chance(reason, random, RevivalConfig.MUSHROOMCOPTER_SPAWN_CHANCE::get);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_AIMING, false);
    }

    public boolean isAiming() {
        return this.entityData.get(DATA_AIMING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ShootGoal());
        this.goalSelector.addGoal(3, new KeepDistanceGoal());
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

    /** Ground distance below it (up to 8 blocks). */
    private double heightAboveGround() {
        BlockPos.MutableBlockPos p = this.blockPosition().mutable();
        for (int i = 0; i < 8; i++) {
            p.move(0, -1, 0);
            if (!this.level().getBlockState(p).getCollisionShape(this.level(), p).isEmpty()) {
                return this.getY() - (p.getY() + 1);
            }
        }
        return 8.0;
    }

    /** Speeds up the propeller and tips forward, then fires one spore if the target is still in sight. */
    private class ShootGoal extends Goal {
        private int ticks;

        ShootGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Mushroomcopter.this.getTarget();
            return Mushroomcopter.this.shotCooldown <= 0 && target != null && target.isAlive()
                    && Mushroomcopter.this.distanceToSqr(target) < SHOOT_RANGE * SHOOT_RANGE && Mushroomcopter.this.getSensing().hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks > 0 && Mushroomcopter.this.getTarget() != null && Mushroomcopter.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.ticks = AIM_TICKS;
            Mushroomcopter.this.entityData.set(DATA_AIMING, true);
            Mushroomcopter.this.level().broadcastEntityEvent(Mushroomcopter.this, EVENT_ACTION_A);
        }

        @Override
        public void stop() {
            Mushroomcopter.this.entityData.set(DATA_AIMING, false);
            Mushroomcopter.this.shotCooldown = 30 + Mushroomcopter.this.random.nextInt(20);
            this.ticks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = Mushroomcopter.this.getTarget();
            if (target == null) {
                return;
            }
            Mushroomcopter.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.ticks == 0 && Mushroomcopter.this.getSensing().hasLineOfSight(target)) {
                Mushroomcopter.this.shoot(target);
            }
        }
    }

    /** Fires one spore at the target. */
    public SporeShot shoot(LivingEntity target) {
        SporeShot shot = new SporeShot(this.level(), this, new ItemStack(ModItems.MUSHROOM_SPORE.get()));
        Vec3 from = this.position().add(0.0, this.getBbHeight() * 0.35, 0.0).add(Vec3.directionFromRotation(0.0F, this.getYRot()).scale(0.5));
        shot.setPos(from.x, from.y, from.z);
        double dx = target.getX() - from.x;
        double dy = target.getY(0.5) - from.y;
        double dz = target.getZ() - from.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        shot.shoot(dx, dy + dist * 0.08, dz, 1.1F, 4.0F);
        this.playSound(ModSounds.MUSHROOMCOPTER_SHOOT.get(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
        this.level().addFreshEntity(shot);
        return shot;
    }

    /** Hovers after its target at a shooting distance of about 6 to 12 blocks, a few blocks above it. */
    private class KeepDistanceGoal extends Goal {
        KeepDistanceGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Mushroomcopter.this.getTarget() != null && Mushroomcopter.this.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = Mushroomcopter.this.getTarget();
            if (target == null) {
                return;
            }
            double d = Mushroomcopter.this.distanceTo(target);
            Vec3 away = Mushroomcopter.this.position().subtract(target.position()).multiply(1.0, 0.0, 1.0);
            away = away.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : away.normalize();
            Vec3 wanted = null;
            if (d > 12.0 || !Mushroomcopter.this.getSensing().hasLineOfSight(target)) {
                wanted = target.position().add(away.scale(7.0));
            } else if (d < 6.0) {
                wanted = Mushroomcopter.this.position().add(away.scale(4.0));
            }
            if (wanted != null) {
                Mushroomcopter.this.getMoveControl().setWantedPosition(wanted.x, target.getY() + 3.0, wanted.z, 1.0);
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.shotCooldown > 0) {
            this.shotCooldown--;
        }
        // Hover about three blocks above the ground.
        double h = heightAboveGround();
        Vec3 v = this.getDeltaMovement();
        if (this.getTarget() == null && h > 3.5) {
            this.setDeltaMovement(v.x, Math.min(v.y, -0.02), v.z);
        } else if (h < 2.0) {
            this.setDeltaMovement(v.x, Math.max(v.y, 0.05), v.z);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level().isClientSide()) {
            this.aimO = this.aim;
            this.aim = Mth.approach(this.aim, this.isAiming() ? 1.0F : 0.0F, this.isAiming() ? 1.0F / 8.0F : 0.2F);
            this.spinO = this.spin;
            this.spin += 0.9F + 1.1F * this.aim;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // A puff of spores while it aims.
        if (this.level().isClientSide() && this.isAiming() && this.random.nextInt(2) == 0) {
            this.level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, this.getRandomX(0.6), this.getY(0.3), this.getRandomZ(0.6), 0.0, -0.02, 0.0);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.aimAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Aiming before a shot. */
    @Override
    public AnimationState actionA() {
        return this.aimAnimation;
    }

    /** How far into the aim it is (propeller fast, body tipped forward), 0 to 1. */
    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.aimO, this.aim);
    }

    /** Propeller angle in radians (client only), passed to the model through {@link #roll}. */
    @Override
    public float roll(float partialTick) {
        return Mth.lerp(partialTick, this.spinO, this.spin);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ShotCooldown", this.shotCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.shotCooldown = input.getIntOr("ShotCooldown", 0);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 40;
    }

    /** The whir of the propeller. */
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MUSHROOMCOPTER.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MUSHROOMCOPTER.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUSHROOMCOPTER.death().get();
    }
}
