package com.crazyalloy.revival.client.renderer;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.client.model.RevivalRenderState;
import com.crazyalloy.revival.entity.AnimatedMob;
import com.crazyalloy.revival.entity.FlavoredMob;
import com.crazyalloy.revival.entity.IceCreamFlavor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;

/**
 * Renderer for the animated creatures: one texture (or one per ice cream flavour for a {@link FlavoredMob}), an
 * optional scale, and the shared animation state.
 */
public class RevivalMobRenderer<T extends Mob, M extends EntityModel<RevivalRenderState>> extends MobRenderer<T, RevivalRenderState, M> {
    private final Identifier[] textures;
    private final float scale;

    public RevivalMobRenderer(EntityRendererProvider.Context context, M model, float shadow, String name, float scale) {
        this(context, model, shadow, new String[] {name}, scale);
    }

    /** Several textures, indexed by {@link RevivalRenderState#variant}. */
    public RevivalMobRenderer(EntityRendererProvider.Context context, M model, float shadow, String[] names, float scale) {
        super(context, model, shadow * scale);
        this.textures = java.util.Arrays.stream(names).map(n -> CrazyAlloyRevival.id("textures/entity/" + n + ".png")).toArray(Identifier[]::new);
        this.scale = scale;
    }

    /** "ice_cream_zombie" gives ice_cream_zombie_chocolate, ..._vanilla, ..._strawberry, ..._mint (in flavour order). */
    public static String[] flavorTextures(String base) {
        return java.util.Arrays.stream(IceCreamFlavor.values()).map(f -> base + "_" + f.id()).toArray(String[]::new);
    }

    @Override
    public RevivalRenderState createRenderState() {
        return new RevivalRenderState();
    }

    @Override
    public void extractRenderState(T entity, RevivalRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.attackAnim = entity.getAttackAnim(partialTick);
        state.offHandSwing = entity.swingingArm == InteractionHand.OFF_HAND;
        state.aggressive = entity.isAggressive();
        state.onGround = entity.onGround();
        state.variant = entity instanceof FlavoredMob flavored ? flavored.flavor().ordinal() : 0;
        if (entity instanceof AnimatedMob animated) {
            state.actionA.copyFrom(animated.actionA());
            state.actionB.copyFrom(animated.actionB());
            state.stance = animated.stance(partialTick);
            state.roll = animated.roll(partialTick);
        }
    }

    @Override
    protected void scale(RevivalRenderState state, PoseStack poseStack) {
        float s = state.isBaby ? this.scale * 0.5F : this.scale;
        if (s != 1.0F) {
            poseStack.scale(s, s, s);
        }
    }

    @Override
    public Identifier getTextureLocation(RevivalRenderState state) {
        return textures[Math.min(state.variant, textures.length - 1)];
    }
}
