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
 * Mushroom Man: a thin, dark grey humanoid (long narrow torso, narrow arms close to the body, two straight legs)
 * under a big red cap with light spots. The cap reaches out past the sides of the head, its lower edge is a wide line
 * over the face, and its top steps in twice. Two rectangular light yellow eyes (also drawn by the glow layer).
 * <p>
 * Animations (revival proposal): a springy walk with a little sway, the cap bobbing and wobbling a beat behind the
 * body, a slight idle wobble of the cap, arms held a little forward while hunting, and a two-armed swipe (action A
 * or the attack swing). Texture 64x64; cubes are tagged for tools/gen_textures_v9.py.
 */
public class MushroomManModel extends EntityModel<RevivalRenderState> {
    private final ModelPart torso, head, cap, rightArm, leftArm, rightLeg, leftLeg;

    public MushroomManModel(ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.head = torso.getChild("head");
        this.cap = head.getChild("cap");
        this.rightArm = torso.getChild("right_arm");
        this.leftArm = torso.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition torso = root.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 41).addBox(-3.0F, -10.0F, -1.5F, 6, 10, 3), // tex:torso
                PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6) // tex:head
                .texOffs(24, 0).addBox(-2.5F, -3.5F, -3.3F, 2, 1, 1) // tex:eye
                .texOffs(24, 0).addBox(0.5F, -3.5F, -3.3F, 2, 1, 1), // tex:eye
                PartPose.offset(0.0F, -10.0F, 0.0F));
        head.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-7.0F, -1.0F, -7.0F, 14, 3, 14) // tex:cap_brim
                .texOffs(0, 29).addBox(-5.0F, -3.0F, -5.0F, 10, 2, 10) // tex:cap_top
                .texOffs(40, 29).addBox(-3.0F, -5.0F, -3.0F, 6, 2, 6), // tex:cap_dome
                PartPose.offset(0.0F, -6.0F, 0.0F));
        torso.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(18, 41).addBox(-1.0F, -1.0F, -1.0F, 2, 11, 2), // tex:arm
                PartPose.offsetAndRotation(-4.0F, -9.0F, 0.0F, 0.0F, 0.0F, 0.05F));
        torso.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(18, 41).addBox(-1.0F, -1.0F, -1.0F, 2, 11, 2), // tex:arm
                PartPose.offsetAndRotation(4.0F, -9.0F, 0.0F, 0.0F, 0.0F, -0.05F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(26, 41).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), // tex:leg
                PartPose.offset(-1.5F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(26, 41).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), // tex:leg
                PartPose.offset(1.5F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.7F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float hunt = state.stance;

        float swing = Mth.cos(pos) * 0.8F * spd;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        rightArm.xRot = -swing * 0.5F - 0.35F * hunt;
        leftArm.xRot = swing * 0.5F - 0.35F * hunt;
        rightArm.zRot = 0.05F + Mth.sin(t * 0.07F) * 0.03F;
        leftArm.zRot = -0.05F - Mth.sin(t * 0.07F) * 0.03F;

        // Springy step with a little sway.
        torso.y = 12.0F - Math.abs(Mth.sin(pos)) * 0.9F * spd;
        torso.zRot = Mth.cos(pos) * 0.06F * spd;
        torso.xRot = 0.08F * hunt;
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.7F;
        // The cap wobbles a beat behind the body, plus a small idle wobble.
        cap.zRot = Mth.sin(pos - 0.9F) * 0.1F * spd + Mth.sin(t * 0.09F) * 0.025F;
        cap.xRot = Mth.cos(pos * 2.0F - 0.9F) * 0.05F * spd + Mth.cos(t * 0.07F) * 0.02F;
        cap.y = -6.0F + Math.abs(Mth.sin(pos - 0.6F)) * 0.5F * spd;

        // Two-armed swipe: arms up, then down and in.
        float a = state.ticksSince(state.actionA);
        float k = -1.0F;
        if (a >= 0.0F && a < 10.0F) {
            k = a / 10.0F;
        } else if (state.attackAnim > 0.0F) {
            k = state.attackAnim;
        }
        if (k >= 0.0F) {
            float up = k < 0.4F ? k / 0.4F : 1.0F - (k - 0.4F) / 0.6F;
            float down = k < 0.4F ? 0.0F : Mth.sin((k - 0.4F) / 0.6F * Mth.PI);
            rightArm.xRot = Mth.lerp(up, rightArm.xRot, -2.4F) + down * 0.6F;
            leftArm.xRot = Mth.lerp(up, leftArm.xRot, -2.4F) + down * 0.6F;
            rightArm.zRot -= 0.3F * down;
            leftArm.zRot += 0.3F * down;
            torso.xRot += 0.2F * down - 0.1F * up;
            cap.xRot += 0.15F * down;
        }
    }
}
