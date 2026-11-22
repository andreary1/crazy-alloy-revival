package com.crazyalloy.revival.item;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.registry.ModAttachments;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Fire, Water, Poison, Air, Lightning and Earth Books, found in the top chest of the Mushroom Base. Using one consumes
 * it and teaches its element for good (saved with the player, kept on death); the element can then be selected and
 * cast with the {@link WizardStaffItem}. Each element has its own advancement. A book whose element is already known is
 * not consumed: the player is told instead.
 * <p>
 * Revival additions: a short learning effect (enchanting particles, a level-up chime) and the element's name on screen.
 */
public class ElementalBookItem extends Item {
    public enum Element {
        FIRE(25, 0xFFFF7A1A), WATER(20, 0xFF3A8FE8), POISON(25, 0xFF6ABF3A), AIR(15, 0xFFDCEAF2), LIGHTNING(80, 0xFFF2E030), EARTH(30, 0xFF9A6A3A);

        /** Staff cooldown after casting it, in ticks. */
        public final int cooldown;
        /** The element's colour (ARGB), used by the staff menu. */
        public final int color;

        Element(int cooldown, int color) {
            this.cooldown = cooldown;
            this.color = color;
        }

        public String id() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public int bit() {
            return 1 << this.ordinal();
        }

        public Component displayName() {
            return Component.translatable("element.crazyalloy_revival." + this.id());
        }

        public static Element byId(String id) {
            for (Element e : values()) {
                if (e.id().equals(id)) {
                    return e;
                }
            }
            return null;
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

    /** The bit mask of elements {@code player} has learned. */
    public static int learnedMask(Player player) {
        return player.getData(ModAttachments.LEARNED_ELEMENTS);
    }

    public static boolean hasLearned(Player player, Element element) {
        return (learnedMask(player) & element.bit()) != 0;
    }

    /** Teaches {@code element} to {@code player}; false if it was already known. Awards the element's advancement. */
    public static boolean learn(ServerPlayer player, Element element) {
        int mask = learnedMask(player);
        if ((mask & element.bit()) != 0) {
            return false;
        }
        player.setData(ModAttachments.LEARNED_ELEMENTS, mask | element.bit());
        AdvancementHolder adv = player.level().getServer().getAdvancements().get(CrazyAlloyRevival.id("learn_" + element.id()));
        if (adv != null) {
            player.getAdvancements().award(adv, "learned");
        }
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack book = player.getItemInHand(hand);
        if (hasLearned(player, this.element)) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable("message.crazyalloy_revival.book.already_learned", this.element.displayName()).withStyle(ChatFormatting.GRAY));
            }
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            learn(serverPlayer, this.element);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.2, player.getZ(), 40, 0.5, 0.6, 0.5, 0.6);
            serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 12, 0.4, 0.5, 0.4, 0.03);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7F, 1.4F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.sendOverlayMessage(Component.translatable("message.crazyalloy_revival.book.learned", this.element.displayName())
                    .withStyle(s -> s.withColor(this.element.color & 0xFFFFFF)));
            player.awardStat(Stats.ITEM_USED.get(this));
            book.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }
}
