package com.crazyalloy.revival.entity.projectile;

import com.crazyalloy.revival.item.ElementalBookItem.Element;
import com.crazyalloy.revival.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The spell cast by an elemental book (revival proposal: the original's books are only known by name). It flies
 * straight (no gravity) for up to 2 seconds, trails particles of its element and acts on what it hits:
 * <ul>
 * <li>Fire: 5 damage and sets the target on fire for 5 seconds.</li>
 * <li>Water: 3 damage, puts out burning targets and fire around the impact, strong knockback; 4 more damage to
 * creatures hurt by water (endermen, blazes, striders...).</li>
 * <li>Poison: 2 damage and Poison II for 6 seconds.</li>
 * <li>Air: 1 damage and a gust that throws the target up and away.</li>
 * <li>Lightning: calls a lightning bolt down where it lands.</li>
 * <li>Earth: 6 damage and Slowness II for 4 seconds.</li>
 * </ul>
 * It never breaks blocks; fire from a lightning bolt follows the usual fire rules.
 */
public class SpellBolt extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> DATA_ELEMENT = SynchedEntityData.defineId(SpellBolt.class, EntityDataSerializers.INT);
    private static final int LIFETIME = 40;

    public SpellBolt(EntityType<? extends SpellBolt> type, Level level) {
        super(type, level);
    }

    public SpellBolt(Level level, LivingEntity owner, Element element) {
        super(ModEntities.SPELL_BOLT.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.entityData.set(DATA_ELEMENT, element.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, 0);
    }

    public Element element() {
        Element[] all = Element.values();
        return all[Math.floorMod(this.entityData.get(DATA_ELEMENT), all.length)];
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(trail(), this.getRandomX(0.3), this.getRandomY(), this.getRandomZ(0.3), 0.0, 0.0, 0.0);
            }
        } else if (this.tickCount > LIFETIME) {
            this.discard();
        }
    }

    private ParticleOptions trail() {
        return switch (this.element()) {
            case FIRE -> this.random.nextBoolean() ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME;
            case WATER -> this.random.nextBoolean() ? ParticleTypes.SPLASH : ParticleTypes.BUBBLE_POP;
            case POISON -> new DustParticleOptions(0x5ab83a, 1.0F);
            case AIR -> ParticleTypes.CLOUD;
            case LIGHTNING -> ParticleTypes.ELECTRIC_SPARK;
            case EARTH -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState());
        };
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel level) || !(hitResult.getEntity() instanceof LivingEntity target)) {
            return;
        }
        Entity owner = this.getOwner();
        var magic = this.damageSources().indirectMagic(this, owner);
        Vec3 dir = this.getDeltaMovement().multiply(1.0, 0.0, 1.0);
        dir = dir.lengthSqr() < 1.0E-6 ? Vec3.ZERO : dir.normalize();
        switch (this.element()) {
            case FIRE -> {
                target.hurtServer(level, magic, 5.0F);
                target.igniteForSeconds(5.0F);
            }
            case WATER -> {
                float damage = target.isSensitiveToWater() ? 7.0F : 3.0F;
                target.hurtServer(level, magic, damage);
                target.extinguishFire();
                target.knockback(1.5, -dir.x, -dir.z);
            }
            case POISON -> {
                target.hurtServer(level, magic, 2.0F);
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1), owner);
            }
            case AIR -> {
                target.hurtServer(level, magic, 1.0F);
                target.knockback(2.0, -dir.x, -dir.z);
                target.push(0.0, 0.8, 0.0);
                target.hurtMarked = true;
            }
            case LIGHTNING -> { /* the bolt comes down in onHit */ }
            case EARTH -> {
                target.hurtServer(level, magic, 6.0F);
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1), owner);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 at = hitResult.getLocation();
        switch (this.element()) {
            case LIGHTNING -> {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    bolt.snapTo(at.x, at.y, at.z);
                    if (this.getOwner() instanceof ServerPlayer player) {
                        bolt.setCause(player);
                    }
                    level.addFreshEntity(bolt);
                }
            }
            case WATER -> {
                // Puts out fire around the impact.
                BlockPos centre = BlockPos.containing(at);
                for (BlockPos p : BlockPos.betweenClosed(centre.offset(-2, -1, -2), centre.offset(2, 1, 2))) {
                    if (level.getBlockState(p).getBlock() instanceof BaseFireBlock) {
                        level.removeBlock(p, false);
                    }
                }
                level.sendParticles(ParticleTypes.SPLASH, at.x, at.y, at.z, 30, 0.5, 0.3, 0.5, 0.1);
                this.playSound(SoundEvents.GENERIC_SPLASH, 0.8F, 1.2F);
            }
            case FIRE -> level.sendParticles(ParticleTypes.FLAME, at.x, at.y, at.z, 16, 0.3, 0.3, 0.3, 0.03);
            case POISON -> level.sendParticles(new DustParticleOptions(0x5ab83a, 1.2F), at.x, at.y, at.z, 20, 0.4, 0.4, 0.4, 0.0);
            case AIR -> {
                level.sendParticles(ParticleTypes.GUST, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
                this.playSound(SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), 0.7F, 1.2F);
            }
            case EARTH -> {
                level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()), at.x, at.y, at.z, 30, 0.5, 0.3, 0.5, 0.15);
                this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0F, 0.7F);
            }
        }
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Element", this.entityData.get(DATA_ELEMENT));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(DATA_ELEMENT, input.getIntOr("Element", 0));
    }
}
