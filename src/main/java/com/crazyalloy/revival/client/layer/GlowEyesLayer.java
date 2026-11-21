package com.crazyalloy.revival.client.layer;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.client.model.RevivalRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Glowing eyes, drawn at full brightness over the model from a texture that only has the eye pixels
 * (textures/entity/NAME_eyes.png), the way vanilla spiders' eyes glow. The eyes do not light up the blocks around.
 */
public class GlowEyesLayer<M extends EntityModel<RevivalRenderState>> extends EyesLayer<RevivalRenderState, M> {
    private final RenderType type;

    public GlowEyesLayer(RenderLayerParent<RevivalRenderState, M> renderer, String name) {
        super(renderer);
        this.type = RenderTypes.eyes(CrazyAlloyRevival.id("textures/entity/" + name + "_eyes.png"));
    }

    @Override
    public RenderType renderType() {
        return this.type;
    }
}
