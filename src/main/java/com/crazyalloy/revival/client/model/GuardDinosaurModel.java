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
 * Guard Dinosaur: a green biped leaning forward on strong bent legs, wide feet with white claws, short arms held in
 * front, a big head with a long rectangular snout and a tail sticking out behind. It wears a black jacket (the torso)
 * with a white collar round the neck, a long red tie and narrow black sunglasses.
 * <p>
 * Animations: the head slowly scans the room while idle; heavy, bobbing steps; it leans in when hunting; a bite and a
 * two-handed swipe to attack; "straighten the tie" (action A) and the alarm roar (action B).
 * Texture 128x64; cubes are tagged for tools/gen_textures_v7.py.
 */
public class GuardDinosaurModel extends EntityModel<RevivalRenderState> {
    private static final float BODY_LEAN = 0.3F;
    private static final float THIGH = -0.35F, SHIN = 0.65F, FOOT = -0.3F;
    private static final float ARM = -0.9F, FOREARM = -0.7F;
    private static final float NECK = -0.1F, HEAD = -0.25F;
    private static final float TAIL = -0.15F;

    private final ModelPart body, neck, head, rightArm, leftArm, rightForearm, leftForearm;
    private final ModelPart tail1, tail2, tail3;
    private final ModelPart rightLeg, leftLeg, rightShin, leftShin, rightFoot, leftFoot;

    public GuardDinosaurModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightForearm = rightArm.getChild("forearm");
        this.leftForearm = leftArm.getChild("forearm");
        this.tail1 = body.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightShin = rightLeg.getChild("shin");
        this.leftShin = leftLeg.getChild("shin");
        this.rightFoot = rightShin.getChild("foot");
        this.leftFoot = leftShin.getChild("foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -14.0F, -3.5F, 9, 15, 7) // tex:torso
                .texOffs(72, 35).addBox(-1.0F, -13.0F, -4.5F, 2, 10, 1), // tex:tie
                PartPose.offsetAndRotation(0.0F, 10.5F, 1.0F, BODY_LEAN, 0.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-2.5F, -5.0F, -2.5F, 5, 6, 5) // tex:neck
                .texOffs(52, 0).addBox(-3.0F, -1.0F, -3.0F, 6, 2, 6), // tex:collar
                PartPose.offsetAndRotation(0.0F, -14.0F, -1.0F, NECK, 0.0F, 0.0F));
        neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(76, 0).addBox(-4.0F, -7.0F, -5.0F, 8, 7, 8) // tex:head
                .texOffs(0, 22).addBox(-3.0F, -5.0F, -13.0F, 6, 5, 8) // tex:snout
                .texOffs(108, 0).addBox(-4.5F, -6.5F, -6.0F, 9, 2, 1) // tex:glasses
                .texOffs(108, 4).addBox(4.0F, -6.0F, -5.0F, 1, 1, 4) // tex:glasses_arm
                .texOffs(108, 4).mirror().addBox(-5.0F, -6.0F, -5.0F, 1, 1, 4), // tex:glasses_arm
                PartPose.offsetAndRotation(0.0F, -4.5F, -0.5F, HEAD, 0.0F, 0.0F));

        for (int side = -1; side <= 1; side += 2) {
            PartDefinition arm = body.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(84, 22).addBox(-1.0F, -1.0F, -1.5F, 2, 5, 3), // tex:upper_arm
                    PartPose.offsetAndRotation(side * 5.0F, -11.0F, -2.0F, ARM, 0.0F, side * -0.15F));
            PartDefinition forearm = arm.addOrReplaceChild("forearm", CubeListBuilder.create()
                    .texOffs(94, 22).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2), // tex:forearm
                    PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, FOREARM, 0.0F, 0.0F));
            forearm.addOrReplaceChild("claws", CubeListBuilder.create()
                    .texOffs(102, 22).addBox(-1.0F, 0.0F, -1.0F, 2, 1, 1), // tex:hand_claw
                    PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition leg = root.addOrReplaceChild(side < 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(28, 22).addBox(-2.0F, -1.0F, -2.5F, 4, 7, 5), // tex:thigh
                    PartPose.offsetAndRotation(side * 3.0F, 10.5F, 1.5F, THIGH, 0.0F, 0.0F));
            PartDefinition shin = leg.addOrReplaceChild("shin", CubeListBuilder.create()
                    .texOffs(46, 22).addBox(-1.5F, 0.0F, -1.5F, 3, 7, 3), // tex:shin
                    PartPose.offsetAndRotation(0.0F, 5.5F, 0.5F, SHIN, 0.0F, 0.0F));
            shin.addOrReplaceChild("foot", CubeListBuilder.create()
                    .texOffs(58, 22).addBox(-2.0F, 0.0F, -4.5F, 4, 2, 6) // tex:foot
                    .texOffs(78, 22).addBox(-2.0F, 1.0F, -6.5F, 1, 1, 2) // tex:claw
                    .texOffs(78, 22).addBox(-0.5F, 1.0F, -6.5F, 1, 1, 2) // tex:claw
                    .texOffs(78, 22).addBox(1.0F, 1.0F, -6.5F, 1, 1, 2), // tex:claw
                    PartPose.offsetAndRotation(0.0F, 6.5F, 0.0F, FOOT, 0.0F, 0.0F));
        }

        PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create()
                .texOffs(0, 35).addBox(-3.0F, -2.5F, 0.0F, 6, 5, 8), // tex:tail1
                PartPose.offsetAndRotation(0.0F, -1.5F, 2.5F, TAIL - BODY_LEAN, 0.0F, 0.0F));
        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
                .texOffs(28, 35).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 8), // tex:tail2
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.5F, 0.1F, 0.0F, 0.0F));
        tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
                .texOffs(52, 35).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 7), // tex:tail3
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.5F, 0.1F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.6F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float hunt = state.stance;

        // Heavy steps: big leg swings, the knee folds as the foot lifts, the whole body bobs and rolls.
        for (int side = 0; side < 2; side++) {
            float phase = side == 0 ? pos : pos + Mth.PI;
            ModelPart leg = side == 0 ? rightLeg : leftLeg;
            ModelPart shin = side == 0 ? rightShin : leftShin;
            ModelPart foot = side == 0 ? rightFoot : leftFoot;
            float swing = Mth.cos(phase) * 0.7F * spd;
            float lift = Math.max(0.0F, Mth.sin(phase)) * 0.7F * spd;
            leg.xRot = THIGH + swing - lift * 0.4F;
            shin.xRot = SHIN + lift;
            foot.xRot = FOOT - swing * 0.6F - lift * 0.4F;
        }
        float bob = Math.abs(Mth.cos(pos)) * 1.2F * spd;
        body.y = 10.5F + bob;
        rightLeg.y = leftLeg.y = 10.5F + bob * 0.5F;
        body.xRot = BODY_LEAN + 0.12F * hunt + Mth.sin(t * 0.06F) * 0.015F;
        body.zRot = Mth.cos(pos) * 0.07F * spd;
        body.yRot = 0.0F;

        // Head: follows the look direction; while idle it sweeps slowly from side to side, watching the room.
        float scan = hunt > 0.5F ? 0.0F : Mth.sin(t * 0.025F) * 0.55F * (1.0F - hunt) * (1.0F - spd);
        head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.6F + scan;
        neck.yRot = state.yRot * Mth.DEG_TO_RAD * 0.4F;
        head.xRot = HEAD + state.xRot * Mth.DEG_TO_RAD * 0.7F - body.zRot;
        neck.xRot = NECK - 0.1F * hunt;
        head.zRot = -body.zRot;

        // Arms: held in front; a little higher when hunting.
        rightArm.xRot = ARM - 0.25F * hunt + Mth.sin(t * 0.08F) * 0.04F;
        leftArm.xRot = ARM - 0.25F * hunt - Mth.sin(t * 0.08F) * 0.04F;
        rightArm.yRot = leftArm.yRot = 0.0F;
        rightArm.zRot = 0.15F;
        leftArm.zRot = -0.15F;
        rightForearm.xRot = leftForearm.xRot = FOREARM;

        // Tail: sways against the steps and balances the lean.
        tail1.yRot = Mth.sin(t * 0.07F) * 0.08F - Mth.cos(pos) * 0.15F * spd;
        tail2.yRot = tail1.yRot * 0.8F;
        tail3.yRot = tail1.yRot * 0.8F;
        tail1.xRot = TAIL - BODY_LEAN - 0.12F * hunt;

        // Attack: a snapping bite with a two-handed swipe.
        float a = state.attackAnim;
        if (a > 0.0F) {
            float p = Mth.sin(a * Mth.PI);
            body.xRot += 0.25F * p;
            head.xRot += 0.35F * p;
            rightArm.xRot -= 0.9F * p;
            leftArm.xRot -= 0.9F * p;
            rightForearm.xRot += 0.6F * p;
            leftForearm.xRot += 0.6F * p;
        }

        // Action A: straighten the tie. The right hand goes to the knot, tugs it twice, the body stands up straight.
        float s = state.ticksSince(state.actionA);
        if (s >= 0.0F && s < 40.0F) {
            float k = s < 8.0F ? s / 8.0F : s > 32.0F ? (40.0F - s) / 8.0F : 1.0F;
            float tug = s > 10.0F && s < 30.0F ? Mth.sin((s - 10.0F) * Mth.PI / 5.0F) * 0.12F : 0.0F;
            body.xRot -= 0.15F * k;
            rightArm.xRot = Mth.lerp(k, rightArm.xRot, -1.2F) + tug;
            rightArm.zRot = Mth.lerp(k, rightArm.zRot, -0.45F);
            rightForearm.xRot = Mth.lerp(k, rightForearm.xRot, -1.5F);
            head.xRot += 0.35F * k;
            head.yRot *= 1.0F - k;
        }

        // Action B: the alarm. Head thrown back with the snout up, arms spread, tail raised.
        float r = state.ticksSince(state.actionB);
        if (r >= 0.0F && r < 25.0F) {
            float k = r < 5.0F ? r / 5.0F : r > 20.0F ? (25.0F - r) / 5.0F : 1.0F;
            float shake = Mth.sin(r * 2.2F) * 0.05F * k;
            body.xRot -= 0.2F * k;
            neck.xRot -= 0.35F * k;
            head.xRot -= 0.55F * k;
            head.yRot += shake;
            rightArm.xRot -= 0.5F * k;
            leftArm.xRot -= 0.5F * k;
            rightArm.zRot += 0.6F * k;
            leftArm.zRot -= 0.6F * k;
            tail1.xRot += 0.35F * k;
        }
    }
}
