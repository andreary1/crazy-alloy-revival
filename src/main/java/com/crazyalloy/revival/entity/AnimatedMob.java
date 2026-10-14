package com.crazyalloy.revival.entity;

import net.minecraft.world.entity.AnimationState;

/**
 * Creatures with one-shot animations (vanilla AnimationState, started on the client by an entity event sent from
 * the server, so every player who sees the creature plays the same animation). Read by the client renderer.
 */
public interface AnimatedMob {
    /** Entity event ids used by Crazy Alloy creatures; vanilla uses 0 to 69. */
    byte EVENT_ACTION_A = 100;
    byte EVENT_ACTION_B = 101;

    /** First one-shot animation: Soldier shot + reload, King ground slam. */
    AnimationState actionA();

    /** Second one-shot animation: King summoning gesture. */
    default AnimationState actionB() {
        return actionA();
    }

    /** 0 to 1: how far into its aiming or combat stance the creature is (smoothed on the client). */
    default float stance(float partialTick) {
        return 0.0F;
    }

    /** Sideways lean in radians (the Segway tilting into a turn); positive leans to the creature's right. */
    default float roll(float partialTick) {
        return 0.0F;
    }
}
