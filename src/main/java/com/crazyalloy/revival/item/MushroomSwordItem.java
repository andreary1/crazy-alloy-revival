package com.crazyalloy.revival.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Mushroom Sword, found in the chests of the Mushroom Outpost and the Mushroom Base. Iron-like damage (6). Revival
 * proposal: one hit in four releases spores that poison the target for 3 seconds.
 */
public class MushroomSwordItem extends Item {
    public MushroomSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (attacker.getRandom().nextInt(4) == 0) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0), attacker);
        }
    }
}
