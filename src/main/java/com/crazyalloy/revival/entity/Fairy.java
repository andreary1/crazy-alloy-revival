package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Fairy of the Mystic Grove (stage 8): a tiny winged humanoid with a reddish-brown high bun, green eyes, a short green
 * dress and pale wings. Appears in ones or twos and flees from threats: it panics when hurt and keeps away from Evil
 * Fairies and monsters. Using an empty {@link ModItems#JAR Jar} on it puts it in the jar: the jar is used up and the
 * player gets a Jar With A Fairy (which lets it out again on any block).
 * <p>
 * Revival additions: a soft up-and-down drift while flying, gradual turns, fluttering wings and a faint sparkle trail.
 */
public class Fairy extends PathfinderMob {
    public Fairy(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    public static boolean checkSpawnRules(EntityType<Fairy> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isSolid() && SpawnRules.chance(reason, random, RevivalConfig.FAIRY_SPAWN_CHANCE::get);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, EvilFairy.class, 10.0F, 1.2, 1.6));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1.2, 1.5));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return flyingNavigation(this, level);
    }

    static PathNavigation flyingNavigation(PathfinderMob mob, Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(mob, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
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

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.JAR.get()) || !this.isAlive()) {
            return super.mobInteract(player, hand);
        }
        if (this.level() instanceof ServerLevel level) {
            ItemStack jar = new ItemStack(ModItems.JAR_WITH_A_FAIRY.get());
            if (this.hasCustomName()) {
                jar.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, this.getCustomName());
            }
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, jar, false));
            level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(0.5), this.getZ(), 10, 0.2, 0.2, 0.2, 0.02);
            this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.4F);
            this.discard();
        }
        return InteractionResult.SUCCESS;
    }

    /** Lets a fairy out of a jar at {@code pos}. */
    public static void release(ServerLevel level, Vec3 pos, ItemStack jar) {
        Fairy fairy = com.crazyalloy.revival.registry.ModEntities.FAIRY.get().create(level, EntitySpawnReason.BUCKET);
        if (fairy == null) {
            return;
        }
        fairy.snapTo(pos.x, pos.y, pos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
        if (jar.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)) {
            fairy.setCustomName(jar.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME));
        }
        fairy.setPersistenceRequired();
        level.addFreshEntity(fairy);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.3, pos.z, 10, 0.2, 0.2, 0.2, 0.02);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // A faint sparkle trail.
        if (this.level().isClientSide() && this.random.nextInt(8) == 0) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getRandomX(0.4), this.getY(0.3), this.getRandomZ(0.4), 0.0, -0.01, 0.0);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FAIRY.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FAIRY.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FAIRY.death().get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
