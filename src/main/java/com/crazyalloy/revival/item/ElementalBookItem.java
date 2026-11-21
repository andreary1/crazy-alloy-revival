package com.crazyalloy.revival.item;

import com.crazyalloy.revival.entity.projectile.SpellBolt;
import java.util.Locale;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Fire, Water, Poison, Air, Lightning and Earth Books, found in the top chest of the Mushroom Base. The original only
 * gives their names; what they do is a revival proposal: using one casts a {@link SpellBolt} of its element in the
 * direction you look. Each book has {@link #USES} uses and a short cooldown (longer for lightning).
 */
public class ElementalBookItem extends Item {
    public static final int USES = 64;

    public enum Element {
        FIRE(25), WATER(20), POISON(25), AIR(15), LIGHTNING(80), EARTH(30);

        public final int cooldown;

        Element(int cooldown) {
            this.cooldown = cooldown;
        }

        public String id() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private final Element element;

    public ElementalBookItem(Element element, Properties properties) {
        super(properties);
        this.element = element;
    }

    public Element element() {
        return this.element;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack book = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), castSound(), SoundSource.PLAYERS, 0.8F, 1.0F + (level.getRandom().nextFloat() - 0.5F) * 0.2F);
        if (level instanceof ServerLevel serverLevel) {
            SpellBolt bolt = new SpellBolt(serverLevel, player, this.element);
            bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.5F);
            serverLevel.addFreshEntity(bolt);
            book.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        player.getCooldowns().addCooldown(book, this.element.cooldown);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    private SoundEvent castSound() {
        return switch (this.element) {
            case FIRE -> SoundEvents.BLAZE_SHOOT;
            case WATER -> SoundEvents.BUCKET_EMPTY;
            case POISON -> SoundEvents.WITCH_THROW;
            case AIR -> SoundEvents.BREEZE_SHOOT;
            case LIGHTNING -> SoundEvents.EVOKER_CAST_SPELL;
            case EARTH -> SoundEvents.ROOTED_DIRT_PLACE;
        };
    }
}
