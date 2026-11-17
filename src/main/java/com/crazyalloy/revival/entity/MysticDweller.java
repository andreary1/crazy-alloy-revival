package com.crazyalloy.revival.entity;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Shared base of the people who live in the Mystic Grove: the {@link Wizard} in his tower and the {@link Dwarf} in
 * his hut. They are calm, never despawn, run from monsters and from whoever hurts them, and stay close to the home
 * their structure gave them (the template sets {@code FromStructure} in their entity data).
 * <p>
 * Revival proposals: the original shows neither of them trading, so the trades (a merchant screen, like a wandering
 * trader's, with a fixed list per character) are new. So is the idle work animation (action A) they play now and
 * then while standing still.
 */
public abstract class MysticDweller extends AbstractVillager implements AnimatedMob {
    /** Ticks of the idle work animation. */
    public static final int WORK_TICKS = 60;
    private final AnimationState workAnimation = new AnimationState();
    private boolean fromStructure;
    private boolean homeSet;

    protected MysticDweller(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    /** The fixed list of what this character sells and buys. */
    protected abstract List<MerchantOffer> tradeList();

    /** Radius around its home it keeps to when it came from a structure. */
    protected abstract int homeRadius();

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.5, 0.6));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.6));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35));
        this.goalSelector.addGoal(5, new WorkGoal());
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.3));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, net.minecraft.world.entity.Mob.class, 8.0F));
    }

    /** Stops now and then and does a bit of its daily work (reading, stirring, checking its gear). */
    private class WorkGoal extends Goal {
        private int ticks;

        WorkGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !MysticDweller.this.isTrading() && MysticDweller.this.getRandom().nextInt(300) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks < WORK_TICKS && !MysticDweller.this.isTrading();
        }

        @Override
        public void start() {
            this.ticks = 0;
            MysticDweller.this.getNavigation().stop();
            MysticDweller.this.level().broadcastEntityEvent(MysticDweller.this, EVENT_ACTION_A);
        }

        @Override
        public void tick() {
            this.ticks++;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof net.minecraft.world.item.SpawnEggItem || !this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (!this.level().isClientSide()) {
            if (this.getOffers().isEmpty()) {
                return InteractionResult.CONSUME;
            }
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.getOffers();
        offers.addAll(tradeList());
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), 3 + this.random.nextInt(4)));
        }
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    public boolean isFromStructure() {
        return this.fromStructure;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.fromStructure && !this.homeSet) {
            this.homeSet = true;
            this.setHomeTo(this.blockPosition(), homeRadius());
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && this.isTrading()) {
            this.stopTrading();
        }
        return hurt;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ACTION_A) {
            this.workAnimation.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** Idle work animation. */
    @Override
    public AnimationState actionA() {
        return this.workAnimation;
    }

    /** 1 while trading (the character faces the player and holds still), smoothed by the model. */
    @Override
    public float stance(float partialTick) {
        return this.isTrading() ? 1.0F : 0.0F;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("FromStructure", this.fromStructure);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.fromStructure = input.getBooleanOr("FromStructure", false);
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean validTrade) {
        return validTrade ? SoundEvents.WANDERING_TRADER_YES : SoundEvents.WANDERING_TRADER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.WANDERING_TRADER_YES;
    }
}
