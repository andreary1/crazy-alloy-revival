package com.crazyalloy.revival.client.model;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

/** Render state shared by the animated Crazy Alloy creatures. */
public class RevivalRenderState extends LivingEntityRenderState {
    /** Melee swing progress from 0 to 1. */
    public float attackAnim;
    /** True while the swing uses the off hand (left arm). */
    public boolean offHandSwing;
    public boolean aggressive;
    public boolean onGround;
    /** Aiming or combat stance, 0 to 1. */
    public float stance;
    /** Sideways lean in radians (see AnimatedMob#roll). */
    public float roll;
    /** Texture variant (the ice cream flavour of a FlavoredMob). */
    public int variant;
    public final AnimationState actionA = new AnimationState();
    public final AnimationState actionB = new AnimationState();

    /** Ticks since a one-shot animation started, or -1 when it is not running. */
    public float ticksSince(AnimationState state) {
        return state.isStarted() ? state.getTimeInMillis(this.ageInTicks) / 50.0F : -1.0F;
    }
}
