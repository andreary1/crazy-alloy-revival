package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModEntities;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.UUID;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Evil Fairy (stage 8): the hostile variant of the {@link Fairy}, with a dark purple dress, a black bun, red eyes and
 * smoky grey wings. Hunts players and ordinary fairies. Using an Elixir of Life on it turns it into an ordinary fairy:
 * the elixir is used up (the empty bottle is given back) and the player earns The Miracle Of Life.
 * <p>
 * Revival addition: the cure is a short transformation instead of an instant swap. The evil fairy stops attacking,
 * spins in place surrounded by sparkles for two seconds (action A) and then bursts into an ordinary fairy.
 */
public class EvilFairy extends Monster implements AnimatedMob {
    /** Ticks the transformation lasts. */
    public static final int CURE_TICKS = 40;

    private final AnimationState cureAnimation = new AnimationState();
    private int cureTicks;
    private @Nullable UUID curedBy;

    public EvilFairy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    public static boolean checkSpawnRules(EntityType<EvilFairy> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && SpawnRules.chance(reason, random, RevivalConfig.EVIL_FAIRY_SPAWN_CHANCE::get);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Fairy.class, true));
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

    public boolean isCuring() {
        return this.cureTicks > 0;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.ELIXIR_OF_LIFE.get()) || this.isCuring() || !this.isAlive()) {
            return super.mobInteract(player, hand);
        }
        if (this.level() instanceof ServerLevel level) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            startCure(level, player);
        }
        return InteractionResult.SUCCESS;
    }

    /** Begins the transformation into an ordinary fairy; {@code player} earns the advancement when it ends. */
    public void startCure(ServerLevel level, @Nullable Player player) {
        this.cureTicks = CURE_TICKS;
        this.curedBy = player == null ? null : player.getUUID();
        this.setTarget(null);
        this.getNavigation().stop();
        this.setPersistenceRequired();
        level.broadcastEntityEvent(this, EVENT_ACTION_A);
        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 0.7F, 1.8F);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.cureTicks <= 0) {
            return;
        }
        this.setTarget(null);
        this.getNavigation().stop();
        this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        if (this.cureTicks % 4 == 0) {
            level.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY(0.5), this.getZ(), 6, 0.3, 0.4, 0.3, 0.3);
            level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(0.5), this.getZ(), 2, 0.25, 0.3, 0.25, 0.01);
        }
        if (--this.cureTicks == 0) {
            finishCure(level);
        }
    }

    private void finishCure(ServerLevel level) {
        Fairy fairy = this.convertTo(ModEntities.FAIRY.get(), ConversionParams.single(this, false, false), f -> {
            f.setPersistenceRequired();
            level.sendParticles(ParticleTypes.HEART, f.getX(), f.getY(0.8), f.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
            level.sendParticles(ParticleTypes.END_ROD, f.getX(), f.getY(0.5), f.getZ(), 20, 0.3, 0.4, 0.3, 0.05);
        });
        if (fairy == null) {
            return;
        }
        fairy.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.5F);
        if (this.curedBy != null && level.getPlayerByUUID(this.curedBy) instanceof ServerPlayer player) {
            AdvancementHolder adv = level.getServer().getAdvancements().get(CrazyAlloyRevival.id("the_miracle_of_life"));
            if (adv != null) {
                player.getAdvancements().award(adv, "cure");
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        return !this.isCuring() && super.doHurtTarget(level, target);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.cureAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** The transformation spin. */
    @Override
    public AnimationState actionA() {
        return this.cureAnimation;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Dark motes instead of the fairy's sparkles.
        if (this.level().isClientSide() && this.random.nextInt(8) == 0) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(0.4), this.getY(0.3), this.getRandomZ(0.4), 0.0, -0.01, 0.0);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("CureTicks", this.cureTicks);
        if (this.curedBy != null) {
            output.putString("CuredBy", this.curedBy.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.cureTicks = input.getIntOr("CureTicks", 0);
        this.curedBy = input.getString("CuredBy").map(s -> {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.EVIL_FAIRY.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.EVIL_FAIRY.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.EVIL_FAIRY.death().get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }
}
