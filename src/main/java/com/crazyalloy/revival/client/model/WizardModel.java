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
 * Wizard: an old wizard in a blue robe that reaches the feet, with wide sleeves and flared cuffs, a long white beard
 * over the chest that narrows to a point, and a tall blue hat with a wide brim that rises in smaller and smaller
 * segments to a tip bent backwards. The robe and hat carry small square white star marks.
 * <p>
 * Animations (revival proposal): a slow shuffle with the robe swaying and the sleeves swinging a little; the beard
 * and the hat tip sway gently; the idle work (action A) is reading a book held in both hands, then stirring in front
 * of him with one hand; while trading he holds his hands together in front. Texture 128x64; cubes are tagged for
 * tools/gen_textures_v8.py.
 */
public class WizardModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, robe, head, beard, hat2, hat3, tip, rightArm, leftArm;

    public WizardModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.robe = root.getChild("robe");
        this.head = root.getChild("head");
        this.beard = head.getChild("beard");
        ModelPart hat1 = head.getChild("hat1");
        this.hat2 = hat1.getChild("hat2");
        this.hat3 = hat2.getChild("hat3");
        this.tip = hat3.getChild("tip");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-4.0F, 0.0F, -2.5F, 8, 12, 5), // tex:robe_top
                PartPose.ZERO);
        root.addOrReplaceChild("robe", CubeListBuilder.create()
                .texOffs(0, 17).addBox(-5.0F, 0.0F, -3.5F, 10, 12, 7), // tex:robe_skirt
                PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8) // tex:head
                .texOffs(64, 20).addBox(-6.0F, -9.0F, -6.0F, 12, 1, 12), // tex:brim
                PartPose.ZERO);
        head.addOrReplaceChild("beard", CubeListBuilder.create()
                .texOffs(36, 36).addBox(-3.0F, 0.0F, -0.5F, 6, 6, 1) // tex:beard
                .texOffs(50, 36).addBox(-2.0F, 6.0F, -0.5F, 4, 3, 1) // tex:beard2
                .texOffs(50, 40).addBox(-1.0F, 9.0F, -0.5F, 2, 2, 1), // tex:beard3
                PartPose.offset(0.0F, -2.5F, -4.6F));
        PartDefinition hat1 = head.addOrReplaceChild("hat1", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-4.0F, -3.0F, -4.0F, 8, 3, 8), // tex:hat1
                PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition hat2 = hat1.addOrReplaceChild("hat2", CubeListBuilder.create()
                .texOffs(96, 0).addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6), // tex:hat2
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.12F, 0.0F, 0.0F));
        PartDefinition hat3 = hat2.addOrReplaceChild("hat3", CubeListBuilder.create()
                .texOffs(64, 11).addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4), // tex:hat3
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.25F, 0.0F, 0.08F));
        hat3.addOrReplaceChild("tip", CubeListBuilder.create()
                .texOffs(80, 11).addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2), // tex:hat4
                PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -0.45F, 0.0F, 0.1F));

        for (int side = -1; side <= 1; side += 2) {
            root.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(34, 17).addBox(-2.5F, -1.5F, -2.5F, 5, 11, 5) // tex:sleeve
                    .texOffs(0, 36).addBox(-3.0F, 7.0F, -3.0F, 6, 3, 6) // tex:cuff
                    .texOffs(24, 36).addBox(-1.5F, 9.5F, -1.5F, 3, 2, 3), // tex:hand
                    PartPose.offsetAndRotation(side * 6.5F, 1.5F, 0.0F, 0.0F, 0.0F, side * 0.1F));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.6F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float trade = state.stance;

        float swing = Mth.cos(pos) * 0.35F * spd;
        rightArm.xRot = swing + Mth.sin(t * 0.05F) * 0.03F;
        leftArm.xRot = -swing - Mth.sin(t * 0.05F) * 0.03F;
        rightArm.yRot = leftArm.yRot = 0.0F;
        rightArm.zRot = -0.1F;
        leftArm.zRot = 0.1F;
        robe.zRot = Mth.cos(pos) * 0.04F * spd;
        robe.xRot = Math.abs(Mth.sin(pos)) * 0.05F * spd;
        body.y = robe.y - 12.0F + Math.abs(Mth.sin(pos)) * 0.4F * spd;

        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        head.y = body.y;
        beard.xRot = Mth.sin(t * 0.06F) * 0.04F + Math.max(0.0F, -head.xRot) * 0.3F + swing * 0.1F;
        hat2.zRot = Mth.sin(t * 0.04F) * 0.03F;
        hat3.zRot = 0.08F + Mth.sin(t * 0.04F + 0.6F) * 0.04F;
        tip.zRot = 0.1F + Mth.sin(t * 0.04F + 1.2F) * 0.06F + swing * 0.1F;

        // Trading: hands held together in front.
        if (trade > 0.0F) {
            rightArm.xRot = Mth.lerp(trade, rightArm.xRot, -0.75F);
            leftArm.xRot = Mth.lerp(trade, leftArm.xRot, -0.75F);
            rightArm.yRot = -0.35F * trade;
            leftArm.yRot = 0.35F * trade;
        }

        // Idle work: read a book held in both hands (0 to 28), then stir with the right hand (28 to 60).
        float a = state.ticksSince(state.actionA);
        if (a >= 0.0F && a < 60.0F) {
            float in = smooth(a / 6.0F) * (1.0F - smooth((a - 54.0F) / 6.0F));
            if (a < 28.0F) {
                rightArm.xRot = Mth.lerp(in, rightArm.xRot, -1.15F);
                leftArm.xRot = Mth.lerp(in, leftArm.xRot, -1.15F);
                rightArm.yRot = -0.45F * in;
                leftArm.yRot = 0.45F * in;
                head.xRot = Mth.lerp(in, head.xRot, 0.45F);
                head.yRot = Mth.lerp(in, head.yRot, Mth.sin(a * 0.25F) * 0.08F); // eyes moving along the lines
            } else {
                float k = (a - 28.0F) * 0.4F;
                rightArm.xRot = Mth.lerp(in, rightArm.xRot, -0.95F + Mth.sin(k) * 0.15F);
                rightArm.zRot = Mth.lerp(in, rightArm.zRot, Mth.cos(k) * 0.25F);
                leftArm.xRot = Mth.lerp(in, leftArm.xRot, -0.3F);
                head.xRot = Mth.lerp(in, head.xRot, 0.35F);
            }
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }
}
