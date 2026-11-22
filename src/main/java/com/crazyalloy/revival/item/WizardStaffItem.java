package com.crazyalloy.revival.item;

import com.crazyalloy.revival.entity.projectile.SpellBolt;
import com.crazyalloy.revival.item.ElementalBookItem.Element;
import com.crazyalloy.revival.menu.StaffMenu;
import com.crazyalloy.revival.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Wizard Staff: the one instrument for every elemental power. Bought from the Wizard (64 Shadow Fragments and 32 Ent
 * Hearts). Shift + right click opens the power menu (locked elements shown with a padlock); right click casts the
 * selected element as a {@link SpellBolt}, if the player has learned it from its book.
 * <p>
 * Revival additions: the staff's tip glows in the selected element's colour (item model chosen from custom model data),
 * one shared casting effect with a sound and particles per element, and a cooldown per cast instead of durability.
 */
public class WizardStaffItem extends Item {
    public WizardStaffItem(Properties properties) {
        super(properties);
    }

    public static @Nullable Element selected(ItemStack staff) {
        String id = staff.get(ModDataComponents.STAFF_ELEMENT.get());
        return id == null ? null : Element.byId(id);
    }

    /** Selects {@code element} on {@code staff}; the custom model data string makes the tip glow in its colour. */
    public static void select(ItemStack staff, Element element) {
        staff.set(ModDataComponents.STAFF_ELEMENT.get(), element.id());
        staff.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(element.id()), List.of()));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack staff = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new StaffMenu(id, inventory, hand),
                        Component.translatable("gui.crazyalloy_revival.wizard_staff.title")));
            }
            return InteractionResult.SUCCESS;
        }
        Element element = selected(staff);
        if (element == null || !ElementalBookItem.hasLearned(player, element)) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable(element == null ? "message.crazyalloy_revival.staff.none_selected"
                        : "message.crazyalloy_revival.staff.not_learned", element == null ? "" : element.displayName()).withStyle(ChatFormatting.GRAY));
            }
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel serverLevel) {
            cast(serverLevel, player, element);
        }
        player.getCooldowns().addCooldown(staff, element.cooldown);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    /** Fires one bolt of {@code element} where {@code player} is looking. */
    public static SpellBolt cast(ServerLevel level, Player player, Element element) {
        SpellBolt bolt = new SpellBolt(level, player, element);
        bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.5F);
        level.addFreshEntity(bolt);
        Vec3 tip = player.getEyePosition().add(player.getLookAngle().scale(0.8));
        level.sendParticles(ParticleTypes.ENCHANT, tip.x, tip.y, tip.z, 8, 0.15, 0.15, 0.15, 0.4);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), castSound(element), SoundSource.PLAYERS, 0.8F,
                1.0F + (level.getRandom().nextFloat() - 0.5F) * 0.2F);
        return bolt;
    }

    private static SoundEvent castSound(Element element) {
        return switch (element) {
            case FIRE -> SoundEvents.BLAZE_SHOOT;
            case WATER -> SoundEvents.BUCKET_EMPTY;
            case POISON -> SoundEvents.WITCH_THROW;
            case AIR -> SoundEvents.BREEZE_SHOOT;
            case LIGHTNING -> SoundEvents.EVOKER_CAST_SPELL;
            case EARTH -> SoundEvents.ROOTED_DIRT_PLACE;
        };
    }
}
