package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CrazyAlloyRevival.MOD_ID);

    /**
     * The elements a player has learned from the elemental books, one bit per element (bit = ordinal). Saved with the
     * player, kept on death and sent to that player's client (the staff menu shows locks from it).
     */
    public static final Supplier<AttachmentType<Integer>> LEARNED_ELEMENTS = ATTACHMENTS.register("learned_elements",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("mask")).copyOnDeath().sync(ByteBufCodecs.VAR_INT).build());

    private ModAttachments() {}
}
