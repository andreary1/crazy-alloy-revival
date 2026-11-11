package com.crazyalloy.revival.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Fairy and Evil Fairy (same shape, different texture): a delicate little body with a relatively big head, a high
 * bun above and behind it, a short flared dress, thin arms and legs and two flat, pale wings on the back.
 * <p>
 * Animations: a soft up-and-down drift and a gentle sway, fast fluttering wings, legs trailing behind while flying,
 * arms that float out a little; the Evil Fairy's cure (action A) spins it around while it rises and its wings open
 * wide. Rendered at 0.6 scale. Texture 64x32; cubes are tagged for tools/gen_textures_v8.py.
 */
public class FairyModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, head, rightArm, leftArm, rightLeg, leftLeg, rightWing, leftWing;

    public FairyModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightLeg = body.getChild("right_leg");
        this.leftLeg = body.getChild("left_leg");
        this.rightWing = body.getChild("right_wing");
        this.leftWing = body.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // The body pivot is at the waist; everything hangs off it so the whole fairy drifts together.
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(28, 0).addBox(-1.5F, -3.0F, -1.0F, 3, 3, 2) // tex:torso
                .texOffs(38, 0).addBox(-2.5F, 0.0F, -2.0F, 5, 3, 4), // tex:skirt
                PartPose.offset(0.0F, 17.0F, 0.0F));
        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5) // tex:head
                .texOffs(20, 0).addBox(-1.0F, -7.0F, 0.5F, 2, 2, 2), // tex:bun
                PartPose.offset(0.0F, -3.0F, 0.0F));
        for (int side = -1; side <= 1; side += 2) {
            body.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(56, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1), // tex:arm
                    PartPose.offsetAndRotation(side * 2.0F, -2.8F, 0.0F, 0.0F, 0.0F, side * 0.2F));
            body.addOrReplaceChild(side < 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(60, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1), // tex:leg
                    PartPose.offset(side * 0.9F, 2.5F, 0.0F));
        }
        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(0, 10).mirror().addBox(-6.0F, -6.0F, 0.0F, 6, 7, 0), // tex:wing
                PartPose.offsetAndRotation(-0.5F, -1.5F, 1.0F, 0.0F, -0.5F, 0.0F));
        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(0, 10).addBox(0.0F, -6.0F, 0.0F, 6, 7, 0), // tex:wing
                PartPose.offsetAndRotation(0.5F, -1.5F, 1.0F, 0.0F, 0.5F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);

        // Soft drift: up and down, with a little sway; leaning into the flight when moving.
        body.y = 15.0F + Mth.sin(t * 0.12F) * 1.2F;
        body.zRot = Mth.sin(t * 0.07F) * 0.06F;
        body.xRot = 0.25F * spd;
        body.yRot = 0.0F;

        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD - body.xRot * 0.6F;

        // Legs trail behind, crossing slowly.
        rightLeg.xRot = 0.35F + 0.3F * spd + Mth.sin(t * 0.1F) * 0.15F;
        leftLeg.xRot = 0.35F + 0.3F * spd - Mth.sin(t * 0.1F) * 0.15F;

        // Arms float out a little; the attack swing (Evil Fairy) slaps with the right arm.
        rightArm.xRot = -0.2F + Mth.sin(t * 0.09F) * 0.15F;
        leftArm.xRot = -0.2F - Mth.sin(t * 0.09F) * 0.15F;
        rightArm.zRot = 0.35F + Mth.cos(t * 0.11F) * 0.1F;
        leftArm.zRot = -0.35F - Mth.cos(t * 0.11F) * 0.1F;
        if (state.attackAnim > 0.0F) {
            float p = Mth.sin(state.attackAnim * Mth.PI);
            rightArm.xRot -= 1.8F * p;
            body.yRot = -0.3F * p;
        }

        // Wings: fast flutter around their hinge on the back.
        float flap = Mth.sin(t * 1.7F) * 0.55F;
        rightWing.yRot = -0.55F - flap;
        leftWing.yRot = 0.55F + flap;
        rightWing.zRot = -0.15F;
        leftWing.zRot = 0.15F;

        // Action A (Evil Fairy cure): spins two full turns while rising, wings spread wide, arms out.
        float s = state.ticksSince(state.actionA);
        if (s >= 0.0F && s < 40.0F) {
            float k = s / 40.0F;
            body.yRot = k * k * Mth.TWO_PI * 2.0F;
            body.y -= 3.0F * Mth.sin(k * Mth.PI);
            rightArm.zRot = 1.2F;
            leftArm.zRot = -1.2F;
            rightWing.yRot = -0.2F - flap * 0.3F;
            leftWing.yRot = 0.2F + flap * 0.3F;
            head.xRot = -0.4F;
        }
    }
}
