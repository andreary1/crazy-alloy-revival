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
 * Troll: a compact, heavy humanoid. A broad bare beige torso with a small dark navel, a big head sitting low between
 * the shoulders, a long rectangular nose sticking far out, small eyes, a wide dark mouth line, big ears spread to the
 * sides, a mossy green tuft on top with strands over the forehead and a green beard, thick arms ending in plain fists,
 * short legs and a skirt of hanging green leaf strips at the waist.
 * <p>
 * Animations: firm steps with the arms swinging and the body rolling; the head bobs with the steps; a slow look
 * around while it stands still (action A); a two-handed overhead smash (action B / attack swing); a slight crouch
 * with the arms forward while fighting. Texture 128x64; cubes are tagged for tools/gen_textures_v8.py.
 */
public class TrollModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, head, rightArm, leftArm, rightLeg, leftLeg, skirt;

    public TrollModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.skirt = body.getChild("skirt");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.0F, -14.0F, -4.0F, 14, 14, 8), // tex:torso
                PartPose.offset(0.0F, 15.0F, 0.0F));
        body.addOrReplaceChild("skirt", CubeListBuilder.create()
                .texOffs(52, 22).addBox(-7.5F, -1.0F, -4.5F, 15, 5, 9), // tex:skirt
                PartPose.ZERO);
        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(44, 0).addBox(-6.0F, -10.0F, -6.0F, 12, 10, 10) // tex:head
                .texOffs(100, 22).addBox(-1.5F, -7.5F, -10.0F, 3, 5, 4) // tex:nose
                .texOffs(114, 22).addBox(6.0F, -8.0F, -2.0F, 3, 4, 1) // tex:ear
                .texOffs(114, 22).mirror().addBox(-9.0F, -8.0F, -2.0F, 3, 4, 1) // tex:ear
                .texOffs(0, 22).addBox(-6.5F, -11.0F, -6.5F, 13, 2, 13) // tex:hair
                .texOffs(0, 37).addBox(-5.0F, -9.0F, -7.0F, 10, 3, 1) // tex:bangs
                .texOffs(22, 37).addBox(-5.0F, -0.5F, -6.5F, 10, 2, 2), // tex:beard
                PartPose.offset(0.0F, -13.0F, -1.0F));

        for (int side = -1; side <= 1; side += 2) {
            body.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(88, 0).addBox(-2.5F, -1.0F, -2.5F, 5, 16, 5), // tex:arm
                    PartPose.offsetAndRotation(side * 9.5F, -12.0F, 0.0F, 0.0F, 0.0F, side * 0.08F));
            root.addOrReplaceChild(side < 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(108, 0).addBox(-2.5F, 0.0F, -2.5F, 5, 9, 5), // tex:leg
                    PartPose.offset(side * 3.5F, 15.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.55F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float fight = state.stance;

        // Firm steps: legs and opposite arms swing, the body rolls and bobs, the head follows the bob.
        float swing = Mth.cos(pos) * 0.75F * spd;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        float bob = Math.abs(Mth.sin(pos)) * 1.0F * spd;
        body.y = 15.0F + bob;
        rightLeg.y = leftLeg.y = 15.0F + bob * 0.3F;
        body.zRot = Mth.cos(pos) * 0.06F * spd;
        body.xRot = 0.12F * fight + Mth.sin(t * 0.05F) * 0.015F;
        rightArm.xRot = -swing * 0.8F - 0.5F * fight + Mth.sin(t * 0.07F) * 0.03F;
        leftArm.xRot = swing * 0.8F - 0.5F * fight - Mth.sin(t * 0.07F) * 0.03F;
        rightArm.zRot = -0.08F - 0.05F * spd;
        leftArm.zRot = 0.08F + 0.05F * spd;
        rightArm.yRot = leftArm.yRot = 0.0F;
        skirt.xRot = -body.xRot * 0.5F + Math.abs(swing) * 0.2F;

        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD - 0.12F * fight + Mth.sin(pos * 2.0F) * 0.04F * spd;
        head.zRot = -body.zRot;

        // Action A: stop and look around, in slow turns with short holds (left, hold, right, hold, centre).
        float s = state.ticksSince(state.actionA);
        if (s >= 0.0F && s < 60.0F) {
            float look;
            if (s < 12.0F) {
                look = smooth(s / 12.0F);
            } else if (s < 22.0F) {
                look = 1.0F;
            } else if (s < 38.0F) {
                look = 1.0F - 2.0F * smooth((s - 22.0F) / 16.0F);
            } else if (s < 48.0F) {
                look = -1.0F;
            } else {
                look = -1.0F + smooth((s - 48.0F) / 12.0F);
            }
            head.yRot = look * 0.85F;
            head.xRot = 0.05F;
        }

        // Smash (action B, or any swing): both arms go up overhead and come down together.
        float a = state.ticksSince(state.actionB);
        float raise = 0.0F;
        if (a >= 0.0F && a < 16.0F) {
            raise = a < 6.0F ? smooth(a / 6.0F) : a < 9.0F ? 1.0F - (a - 6.0F) / 3.0F * 1.15F : -0.15F * (1.0F - (a - 9.0F) / 7.0F);
        } else if (state.attackAnim > 0.0F) {
            raise = Mth.sin(state.attackAnim * Mth.PI);
        }
        if (raise != 0.0F) {
            rightArm.xRot = Mth.lerp(Math.abs(raise), rightArm.xRot, raise > 0 ? -2.9F : -0.4F);
            leftArm.xRot = rightArm.xRot;
            rightArm.zRot = 0.25F * raise;
            leftArm.zRot = -0.25F * raise;
            body.xRot -= 0.15F * raise;
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }
}
