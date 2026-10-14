package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Guard Dinosaur: the Bank's security, a green biped in a black suit, white collar, long red tie and narrow
 * sunglasses. Targets players and fights in melee: 30 health, 5 damage, follows from 16 blocks, partly resists
 * knockback (the original's numbers).
 * <p>
 * Revival additions: while idle it scans the room and now and then straightens its jacket and tie; it walks with
 * heavy steps. Revival proposal ({@link RevivalConfig#GUARD_DINOSAUR_ALERT}): when it first spots a player it stops,
 * roars an alarm and calls the other guards and Segways nearby onto the same player, and only then charges. Being
 * hit skips the pause: it calls the others and fights back at once.
 */
public class GuardDinosaur extends Monster implements AnimatedMob {
    /** Ticks the alarm pause lasts (the alert animation plays over the same time). */
    public static final int ALERT_TICKS = 25;
    /** Ticks the "straighten the tie" animation lasts. */
    public static final int ADJUST_TICKS = 40;

    private final AnimationState adjustAnimation = new AnimationState();
    private final AnimationState alertAnimation = new AnimationState();
    private @Nullable UUID alarmedAbout;
    private boolean fromBank;
    private int adjustCooldown = 200;
    private float stance, stanceO;

    public GuardDinosaur(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AlarmGoal());
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, GuardDinosaur.class, Segway.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** True while the alarm pause for the current target has not happened yet. */
    private boolean needsAlarm() {
        LivingEntity target = this.getTarget();
        return RevivalConfig.GUARD_DINOSAUR_ALERT.get() && target != null && target.isAlive() && !target.getUUID().equals(this.alarmedAbout);
    }

    /** Stop, roar and call the others; then the melee goal takes over. */
    private class AlarmGoal extends Goal {
        private int ticks;

        AlarmGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return needsAlarm();
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks < ALERT_TICKS && GuardDinosaur.this.getTarget() != null && GuardDinosaur.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.ticks = 0;
            GuardDinosaur.this.getNavigation().stop();
            GuardDinosaur.this.level().broadcastEntityEvent(GuardDinosaur.this, EVENT_ACTION_B);
            GuardDinosaur.this.playSound(ModSounds.GUARD_DINOSAUR_ALERT.get(), 1.5F, 1.0F);
        }

        @Override
        public void tick() {
            this.ticks++;
            LivingEntity target = GuardDinosaur.this.getTarget();
            if (target != null) {
                GuardDinosaur.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }

        @Override
        public void stop() {
            LivingEntity target = GuardDinosaur.this.getTarget();
            if (target != null) {
                // Whatever interrupted the pause, the alarm counts as raised for this target.
                raiseAlarm(target);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    /** Marks {@code target} as alarmed about and sends every guard and Segway in range after it. */
    public void raiseAlarm(LivingEntity target) {
        this.alarmedAbout = target.getUUID();
        int radius = RevivalConfig.GUARD_DINOSAUR_ALERT_RADIUS.get();
        if (radius <= 0) {
            return;
        }
        for (Mob other : this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(radius),
                m -> m != this && m.isAlive() && (m instanceof GuardDinosaur || m instanceof Segway) && m.getTarget() == null)) {
            if (other instanceof GuardDinosaur guard) {
                guard.alarmedAbout = target.getUUID(); // it heard the alarm: no second pause
            }
            other.setTarget(target);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && source.getEntity() instanceof LivingEntity attacker && !(attacker instanceof GuardDinosaur) && !(attacker instanceof Segway)) {
            raiseAlarm(attacker);
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
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.tickCount < 5 && this.fromBank && !RevivalConfig.BANK_GUARDS.get()) {
            this.discard();
            return;
        }
        // Idle: now and then it straightens its jacket and tie.
        if (this.getTarget() == null && this.getNavigation().isDone() && --this.adjustCooldown <= 0) {
            this.adjustCooldown = 300 + this.random.nextInt(400);
            level.broadcastEntityEvent(this, EVENT_ACTION_A);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.adjustAnimation.start(this.tickCount);
        } else if (id == EVENT_ACTION_B) {
            this.alertAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** "Straighten the tie" while idle. */
    @Override
    public AnimationState actionA() {
        return this.adjustAnimation;
    }

    /** The alarm roar. */
    @Override
    public AnimationState actionB() {
        return this.alertAnimation;
    }

    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.stanceO, this.stance);
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
        return ModSounds.GUARD_DINOSAUR.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GUARD_DINOSAUR.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GUARD_DINOSAUR.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Heavy steps.
        this.playSound(SoundEvents.RAVAGER_STEP, 0.45F, 1.25F);
    }
}
