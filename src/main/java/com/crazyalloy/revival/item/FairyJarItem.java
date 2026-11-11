package com.crazyalloy.revival.item;

import com.crazyalloy.revival.entity.Fairy;
import com.crazyalloy.revival.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

/**
 * Jar With A Fairy (stage 8). Revival addition: using it on a block lets the fairy out next to that face and gives the
 * empty Jar back, so a caught fairy can be moved somewhere else (a custom name on the jar goes with the fairy).
 */
public class FairyJarItem extends Item {
    public FairyJarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack stack = context.getItemInHand();
        Fairy.release(level, Vec3.atBottomCenterOf(pos).add(0.0, 0.2, 0.0), stack);
        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, 1.0F, 1.3F);
        Player player = context.getPlayer();
        if (player != null) {
            player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, new ItemStack(ModItems.JAR.get())));
        } else {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
