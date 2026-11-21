package com.crazyalloy.revival.entity.projectile;

import com.crazyalloy.revival.registry.ModEntities;
import com.crazyalloy.revival.registry.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Spore fired by the Mushroomcopter: 3 damage and, as a revival proposal, 3 seconds of Poison. Flies almost straight
 * (light gravity) and leaves a faint purple trail.
 */
public class SporeShot extends CandyProjectile {
    public static final float DAMAGE = 3.0F;
    private static final DustParticleOptions TRAIL = new DustParticleOptions(0x9a5ac8, 0.8F);

    public SporeShot(EntityType<? extends SporeShot> type, Level level) {
        super(type, level);
    }

    public SporeShot(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.SPORE_SHOT.get(), owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MUSHROOM_SPORE.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.008;
    }

    @Override
    protected float damage() {
        return DAMAGE;
    }

    @Override
    protected SoundEvent impactSound() {
        return SoundEvents.FUNGUS_BREAK;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (this.level() instanceof ServerLevel && hitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0), this.getOwner());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.level().addParticle(TRAIL, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }
}
