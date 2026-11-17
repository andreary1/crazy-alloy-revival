package com.crazyalloy.revival.item;

import com.crazyalloy.revival.entity.projectile.BulletEntity;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * AK47: an automatic rifle. Hold use to fire a bullet every {@link #FIRE_INTERVAL} ticks; each bullet uses one AK47
 * Ammo from the inventory (none in creative) and one point of durability. Bullets fly fast and almost straight.
 * Revival choice: bullet damage is a config value (ak47BulletDamage); the gun is found in Bank vaults, not crafted.
 */
public class Ak47Item extends Item {
    public static final int FIRE_INTERVAL = 3;

    public Ak47Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (findAmmo(player).isEmpty() && !player.hasInfiniteMaterials()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.6F);
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack gun, int ticksRemaining) {
        if (!(entity instanceof Player player) || (getUseDuration(gun, entity) - ticksRemaining) % FIRE_INTERVAL != 0) {
            return;
        }
        fire(level, player, gun);
    }

    /** Fires one bullet; stops the burst when the ammo runs out. Returns true if a bullet left the barrel. */
    public static boolean fire(Level level, Player player, ItemStack gun) {
        ItemStack ammo = findAmmo(player);
        if (ammo.isEmpty() && !player.hasInfiniteMaterials()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.6F);
            player.stopUsingItem();
            return false;
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.AK47_SHOOT.get(), SoundSource.PLAYERS, 0.8F,
                0.95F + level.getRandom().nextFloat() * 0.1F);
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(BulletEntity::new, serverLevel, new ItemStack(ModItems.AK47_AMMO.get()), player, 0.0F, 4.0F, 1.5F);
            gun.hurtAndBreak(1, player, player.getUsedItemHand());
        }
        if (!player.hasInfiniteMaterials()) {
            ammo.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(gun.getItem()));
        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    private static ItemStack findAmmo(Player player) {
        if (player.getOffhandItem().is(ModItems.AK47_AMMO.get())) {
            return player.getOffhandItem();
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.AK47_AMMO.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
