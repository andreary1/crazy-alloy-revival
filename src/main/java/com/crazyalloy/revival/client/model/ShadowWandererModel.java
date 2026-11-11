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
 * Shadow Wanderer: a tall, thin, hunched figure in deep purple. A narrow torso leaning forward (nearly black on the
 * front) with small spines on the back; a long head pushed out in front, ending in two long jaws around a dark gap,
 * with two narrow spikes on top; long arms bent forward with big hands of four long, separate, dark-tipped fingers;
 * long legs with sharply bent knees and long feet.
 * <p>
 * Animations (revival proposal): a crouched walk with deep knee bends, swinging arms and twitching fingers; a deeper
 * crouch with the arms raised forward while hunting; a two-handed claw swipe (action A / attack swing); a recoil
 * when a heavy blow lands (action B). Texture 64x64; cubes are tagged for tools/gen_textures_v8.py.
 */
public class ShadowWandererModel extends EntityModel<RevivalRenderState> {
    private static final float HUNCH = 0.6F;
    private static final float THIGH = -0.8F, SHIN = 1.3F, FOOT = -0.5F;
    private static final float UPPER_ARM = -0.7F, FOREARM = -0.9F;

    private final ModelPart torso, head, jaw;
    private final ModelPart[] thigh = new ModelPart[2], shin = new ModelPart[2], foot = new ModelPart[2];
    private final ModelPart[] upperArm = new ModelPart[2], forearm = new ModelPart[2], hand = new ModelPart[2], fingers = new ModelPart[2];

    public ShadowWandererModel(ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.head = torso.getChild("head");
        this.jaw = head.getChild("jaw");
        for (int i = 0; i < 2; i++) {
            String side = i == 0 ? "right" : "left";
            this.thigh[i] = root.getChild(side + "_thigh");
            this.shin[i] = thigh[i].getChild("shin");
            this.foot[i] = shin[i].getChild("foot");
            this.upperArm[i] = torso.getChild(side + "_arm");
            this.forearm[i] = upperArm[i].getChild("forearm");
            this.hand[i] = forearm[i].getChild("hand");
            this.fingers[i] = hand[i].getChild("fingers");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition torso = root.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -14.0F, -2.5F, 8, 14, 5) // tex:torso
                .texOffs(50, 20).addBox(-0.5F, -12.0F, 2.0F, 1, 2, 2) // tex:spine
                .texOffs(50, 20).addBox(-0.5F, -7.0F, 2.0F, 1, 2, 2), // tex:spine
                PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, HUNCH, 0.0F, 0.0F));
        PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(28, 0).addBox(-3.0F, -4.0F, -7.0F, 6, 5, 8) // tex:head
                .texOffs(0, 20).addBox(-2.0F, -3.0F, -14.0F, 4, 2, 7) // tex:snout
                .texOffs(44, 20).addBox(-2.5F, -10.0F, -2.0F, 1, 6, 1) // tex:spike
                .texOffs(44, 20).addBox(1.5F, -10.0F, -2.0F, 1, 6, 1), // tex:spike
                PartPose.offsetAndRotation(0.0F, -14.0F, -1.0F, -HUNCH - 0.15F, 0.0F, 0.0F));
        head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(24, 20).addBox(-2.0F, 0.0F, -6.0F, 4, 2, 6), // tex:jaw
                PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, 0.12F, 0.0F, 0.0F));

        for (int i = 0; i < 2; i++) {
            float side = i == 0 ? -1.0F : 1.0F;
            String name = i == 0 ? "right" : "left";
            PartDefinition thigh = root.addOrReplaceChild(name + "_thigh", CubeListBuilder.create()
                    .texOffs(0, 30).addBox(-1.5F, 0.0F, -1.5F, 3, 11, 3), // tex:thigh
                    PartPose.offsetAndRotation(side * 2.5F, 5.5F, 0.0F, THIGH, 0.0F, 0.0F));
            PartDefinition shin = thigh.addOrReplaceChild("shin", CubeListBuilder.create()
                    .texOffs(12, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), // tex:shin
                    PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, SHIN, 0.0F, 0.0F));
            shin.addOrReplaceChild("foot", CubeListBuilder.create()
                    .texOffs(20, 30).addBox(-1.5F, -0.5F, -6.0F, 3, 2, 7), // tex:foot
                    PartPose.offsetAndRotation(0.0F, 11.5F, 0.0F, FOOT, 0.0F, 0.0F));

            PartDefinition arm = torso.addOrReplaceChild(name + "_arm", CubeListBuilder.create()
                    .texOffs(40, 30).addBox(-1.0F, -1.0F, -1.0F, 2, 12, 2), // tex:upper_arm
                    PartPose.offsetAndRotation(side * 5.0F, -12.5F, 0.0F, UPPER_ARM, 0.0F, side * 0.15F));
            PartDefinition forearm = arm.addOrReplaceChild("forearm", CubeListBuilder.create()
                    .texOffs(48, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), // tex:forearm
                    PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, FOREARM, 0.0F, -side * 0.35F));
            PartDefinition hand = forearm.addOrReplaceChild("hand", CubeListBuilder.create()
                    .texOffs(0, 46).addBox(-2.5F, 0.0F, -1.5F, 5, 3, 3), // tex:palm
                    PartPose.offset(0.0F, 11.5F, 0.0F));
            CubeListBuilder f = CubeListBuilder.create();
            for (int k = 0; k < 4; k++) {
                f.texOffs(16, 46).addBox(-2.5F + k * 1.5F, 0.0F, -0.5F, 1, 5, 1); // tex:finger
            }
            hand.addOrReplaceChild("fingers", f, PartPose.offsetAndRotation(0.0F, 2.5F, -0.5F, -0.3F, 0.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.6F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float hunt = state.stance;

        // Crouched walk: thighs swing, the knee bends deeper on the forward swing, arms swing opposite.
        float crouch = 0.15F * hunt;
        for (int i = 0; i < 2; i++) {
            float phase = i == 0 ? 0.0F : Mth.PI;
            float swing = Mth.cos(pos + phase) * 0.55F * spd;
            float lift = Math.max(0.0F, Mth.sin(pos + phase)) * 0.6F * spd;
            thigh[i].xRot = THIGH - crouch + swing - lift * 0.5F;
            shin[i].xRot = SHIN + crouch * 2.0F + lift;
            foot[i].xRot = FOOT - crouch - lift * 0.5F;
            thigh[i].y = 5.5F + 1.5F * hunt;

            float armSwing = -swing * 0.6F;
            upperArm[i].xRot = UPPER_ARM + armSwing - 0.5F * hunt + Mth.sin(t * 0.06F + i) * 0.04F;
            forearm[i].xRot = FOREARM + 0.2F * hunt;
            upperArm[i].zRot = (i == 0 ? -0.15F : 0.15F);
            // Twitching fingers.
            fingers[i].xRot = -0.3F - 0.25F * hunt + Mth.sin(t * 0.35F + i * 1.7F) * 0.12F;
        }
        torso.y = 5.5F + 1.5F * hunt + Math.abs(Mth.sin(pos)) * 0.8F * spd;
        torso.xRot = HUNCH + 0.15F * hunt;
        torso.zRot = Mth.cos(pos) * 0.05F * spd;

        head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.8F;
        head.xRot = -HUNCH - 0.15F - 0.15F * hunt + state.xRot * Mth.DEG_TO_RAD * 0.6F;
        jaw.xRot = 0.12F + 0.15F * hunt + Math.max(0.0F, Mth.sin(t * 0.04F)) * 0.08F;

        // Claw swipe (action A or swing): both arms reach up, then rake down and out.
        float a = state.ticksSince(state.actionA);
        float claw = -1.0F;
        if (a >= 0.0F && a < 12.0F) {
            claw = a / 12.0F;
        } else if (state.attackAnim > 0.0F) {
            claw = state.attackAnim;
        }
        if (claw >= 0.0F) {
            float up = claw < 0.4F ? smooth(claw / 0.4F) : 1.0F - smooth((claw - 0.4F) / 0.6F);
            float rake = claw < 0.4F ? 0.0F : Mth.sin((claw - 0.4F) / 0.6F * Mth.PI);
            for (int i = 0; i < 2; i++) {
                float side = i == 0 ? -1.0F : 1.0F;
                upperArm[i].xRot = Mth.lerp(up, upperArm[i].xRot, -2.6F) + rake * 0.4F;
                forearm[i].xRot = Mth.lerp(up, forearm[i].xRot, -0.3F);
                upperArm[i].zRot += side * 0.5F * rake;
                fingers[i].xRot = -0.9F * up;
            }
            jaw.xRot += 0.35F * up;
            torso.xRot -= 0.2F * up - 0.25F * rake;
        }

        // Recoil from a heavy blow: the body snaps back and the arms fly out.
        float r = state.ticksSince(state.actionB);
        if (r >= 0.0F && r < 10.0F) {
            float k = Mth.sin(r / 10.0F * Mth.PI);
            torso.xRot -= 0.55F * k;
            head.xRot -= 0.3F * k;
            jaw.xRot += 0.3F * k;
            for (int i = 0; i < 2; i++) {
                upperArm[i].zRot += (i == 0 ? -0.7F : 0.7F) * k;
                fingers[i].xRot += 0.4F * k;
            }
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }
}
