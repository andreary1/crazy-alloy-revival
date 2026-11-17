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
 * Ent: a big humanoid tree. Dark wood with blue-green vertical streaks (the mystic log), a long, broad torso, salient
 * angular shoulders, heavy arms in two segments with a leafy twig growing from each forearm, two separate trunk-like
 * legs ending in spread roots, a head rising above the torso with narrow amber eyes, and an irregular crown of leaves
 * in three stepped layers.
 * <p>
 * Animations: slow, heavy steps with the torso leaning into each one and the crown swaying behind; arms hanging and
 * swinging a little; a two-armed sweep as its attack (action A); the rooted pose (stance 1) raises both arms up and out
 * like branches, straightens the body and lets only the leaves move. Rendered at 1.4 scale. Texture 128x128; cubes
 * are tagged for tools/gen_textures_v8.py.
 */
public class EntModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, head, crown, rightArm, leftArm, rightForearm, leftForearm, rightLeg, leftLeg;

    public EntModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.crown = head.getChild("crown");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightForearm = rightArm.getChild("forearm");
        this.leftForearm = leftArm.getChild("forearm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.0F, -18.0F, -4.0F, 14, 18, 8), // tex:torso
                PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(44, 0).addBox(-4.0F, -8.0F, -3.5F, 8, 8, 7), // tex:head
                PartPose.offset(0.0F, -18.0F, -0.5F));
        head.addOrReplaceChild("crown", CubeListBuilder.create()
                .texOffs(0, 26).addBox(-8.0F, -3.0F, -7.5F, 16, 4, 14) // tex:crown1
                .texOffs(60, 26).addBox(-5.0F, -6.0F, -5.5F, 12, 3, 10) // tex:crown2
                .texOffs(104, 26).addBox(-4.0F, -8.0F, -3.0F, 6, 2, 6), // tex:crown3
                PartPose.offset(0.0F, -8.0F, 0.0F));

        for (int side = -1; side <= 1; side += 2) {
            PartDefinition arm = body.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(0, 44).addBox(-3.0F, -1.0F, -3.0F, 6, 12, 6) // tex:upper_arm
                    .texOffs(74, 0).addBox(-4.0F, -3.0F, -4.0F, 8, 5, 8), // tex:shoulder
                    PartPose.offsetAndRotation(side * 10.0F, -15.0F, 0.0F, 0.0F, 0.0F, side * 0.12F));
            PartDefinition forearm = arm.addOrReplaceChild("forearm", CubeListBuilder.create()
                    .texOffs(24, 44).addBox(-2.5F, 0.0F, -2.5F, 5, 12, 5), // tex:forearm
                    PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2F, 0.0F, 0.0F));
            PartDefinition twig = forearm.addOrReplaceChild("twig", CubeListBuilder.create()
                    .texOffs(44, 44).addBox(-1.0F, -5.0F, -1.0F, 2, 5, 2), // tex:twig
                    PartPose.offsetAndRotation(side * 2.0F, 4.0F, 0.0F, 0.0F, 0.0F, side * 0.7F));
            twig.addOrReplaceChild("leaves", CubeListBuilder.create()
                    .texOffs(52, 44).addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4), // tex:twig_leaves
                    PartPose.offset(0.0F, -5.0F, 0.0F));

            PartDefinition leg = root.addOrReplaceChild(side < 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(68, 44).addBox(-3.0F, 0.0F, -3.0F, 6, 14, 6), // tex:leg
                    PartPose.offset(side * 4.0F, 10.0F, 0.0F));
            leg.addOrReplaceChild("roots", CubeListBuilder.create()
                    .texOffs(92, 44).addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8), // tex:roots
                    PartPose.offset(0.0F, 12.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.4F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float rooted = state.stance;
        float free = 1.0F - rooted;

        // Slow, heavy steps; the torso leans into each and rolls; the crown lags behind.
        float swing = Mth.cos(pos) * 0.5F * spd * free;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        float bob = Math.abs(Mth.sin(pos)) * 0.8F * spd * free;
        body.y = 10.0F + bob;
        rightLeg.y = leftLeg.y = 10.0F + bob * 0.3F;
        body.xRot = (0.1F * spd + Mth.sin(t * 0.03F) * 0.02F) * free;
        body.zRot = Mth.cos(pos) * 0.07F * spd * free;
        body.yRot = 0.0F;

        head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.7F * free;
        head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.5F * free - rooted * 0.15F;
        head.zRot = -body.zRot * 0.5F;

        // Foliage: always sways in the wind; a little more when walking.
        crown.zRot = Mth.sin(t * 0.045F) * 0.05F - body.zRot * 0.8F;
        crown.xRot = Mth.sin(t * 0.037F + 1.0F) * 0.04F - body.xRot * 0.5F;

        // Arms: hanging, swinging against the legs; the rooted pose raises them up and out like branches.
        float armSwing = -swing * 0.6F + Mth.sin(t * 0.05F) * 0.03F * free;
        rightArm.xRot = Mth.lerp(rooted, armSwing, -2.7F);
        leftArm.xRot = Mth.lerp(rooted, -armSwing, -2.6F);
        rightArm.zRot = Mth.lerp(rooted, 0.12F, -0.55F);
        leftArm.zRot = Mth.lerp(rooted, -0.12F, 0.6F);
        rightArm.yRot = leftArm.yRot = 0.0F;
        rightForearm.xRot = Mth.lerp(rooted, -0.2F, -0.35F);
        leftForearm.xRot = Mth.lerp(rooted, -0.2F, -0.3F);
        // Rooted: the raised "branches" creak slightly in the wind.
        rightArm.zRot += Mth.sin(t * 0.04F) * 0.03F * rooted;
        leftArm.zRot -= Mth.sin(t * 0.05F + 2.0F) * 0.03F * rooted;

        // Action A: a slow two-armed sweep from the sides to the front.
        float s = state.ticksSince(state.actionA);
        float sweep = s >= 0.0F && s < 20.0F ? Mth.sin(s / 20.0F * Mth.PI) : state.attackAnim > 0.0F ? Mth.sin(state.attackAnim * Mth.PI) : 0.0F;
        if (sweep > 0.0F) {
            rightArm.xRot -= 1.3F * sweep;
            leftArm.xRot -= 1.3F * sweep;
            rightArm.yRot = 0.6F * sweep;
            leftArm.yRot = -0.6F * sweep;
            body.xRot += 0.15F * sweep;
        }
    }
}
