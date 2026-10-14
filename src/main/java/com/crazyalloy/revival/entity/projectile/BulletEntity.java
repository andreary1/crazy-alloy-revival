package com.crazyalloy.revival.entity.projectile;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModEntities;
import com.crazyalloy.revival.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** AK47 bullet: fast, barely drops, gone after a few seconds in the air. */
public class BulletEntity extends CandyProjectile {
    private static final int LIFETIME = 60;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
    }

    public BulletEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.BULLET.get(), owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.AK47_AMMO.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.005;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount > LIFETIME) {
            this.discard();
        }
    }

    @Override
    protected float damage() {
        return RevivalConfig.AK47_BULLET_DAMAGE.get().floatValue();
    }

    @Override
    protected SoundEvent impactSound() {
        return SoundEvents.CHAIN_HIT;
    }
}
