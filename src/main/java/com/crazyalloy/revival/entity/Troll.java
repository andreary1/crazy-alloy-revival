package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModSounds;
import com.crazyalloy.revival.registry.ModTags;
import java.util.EnumSet;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Troll of the Mystic Grove (stage 8): a stocky beige humanoid with a huge rectangular nose, big ears, heavy arms, a
 * mossy green tuft on its head and a skirt of hanging leaves. Spawns alone in the Mystic Grove, walks around and
 * fights back when attacked (it and the trolls nearby). It also hunts every creature in the entity tag
 * {@code crazyalloy_revival:troll_enemies}: in the original those are the Shadow Eye and the Shadow Wanderer, which the
 * revival does not have yet, so the tag lists them as optional entries and the troll starts hunting them as soon as
 * they exist.
 * <p>
 * Revival additions: firm, heavy steps with swinging arms; now and then it stops and turns its head slowly from side
 * to side (action A); its attack is a two-handed overhead smash.
 */
public class Troll extends PathfinderMob implements AnimatedMob {
    /** Ticks of the "stop and look around" pause (action A). */
    public static final int LOOK_TICKS = 60;

    private final AnimationState lookAnimation = new AnimationState();
    private final AnimationState smashAnimation = new AnimationState();
    private float stance, stanceO;

    public Troll(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.8)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23);
    }

    public static boolean checkSpawnRules(EntityType<Troll> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isSolid() && SpawnRules.chance(reason, random, RevivalConfig.TROLL_SPAWN_CHANCE::get);
    }

    /** True for creatures the troll hunts on sight (the shadow creatures of the mystic part). */
    public static boolean isEnemy(LivingEntity target) {
        return target.is(ModTags.Entities.TROLL_ENEMIES);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(5, new LookAroundPauseGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target, level) -> isEnemy(target)));
    }

    /** Stops for a few seconds and slowly turns its head from side to side. */
    private class LookAroundPauseGoal extends Goal {
        private int ticks;

        LookAroundPauseGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return Troll.this.getTarget() == null && Troll.this.getRandom().nextInt(400) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks < LOOK_TICKS && Troll.this.getTarget() == null;
        }

        @Override
        public void start() {
            this.ticks = 0;
            Troll.this.getNavigation().stop();
            Troll.this.level().broadcastEntityEvent(Troll.this, EVENT_ACTION_A);
        }

        @Override
        public void tick() {
            this.ticks++;
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        level.broadcastEntityEvent(this, EVENT_ACTION_B);
        boolean hit = super.doHurtTarget(level, target);
        if (hit) {
            this.playSound(SoundEvents.RAVAGER_ATTACK, 0.8F, 1.3F);
        }
        return hit;
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
            this.lookAnimation.start(this.tickCount);
        } else if (id == EVENT_ACTION_B) {
            this.smashAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Stop and look around. */
    @Override
    public AnimationState actionA() {
        return this.lookAnimation;
    }

    /** Two-handed overhead smash. */
    @Override
    public AnimationState actionB() {
        return this.smashAnimation;
    }

    @Override
    public float stance(float partialTick) {
        return Mth.lerp(partialTick, this.stanceO, this.stance);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TROLL.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TROLL.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TROLL.death().get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.35F, 1.4F);
    }
}
