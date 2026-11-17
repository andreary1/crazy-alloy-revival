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
 * Dwarf: a compact humanoid with a big head, blue eyes, orange hair at the sides joining a long red beard that covers
 * most of the chest (two darker braids hang over the front), red sleeves, a brown front, dark grey trousers, brown
 * boots and a dark cap with a square yellow lamp on the front.
 * <p>
 * Animations (revival proposal): short, brisk steps with the arms swinging; the beard sways; the idle work (action A)
 * is straightening the cap with one hand, then stroking the beard with the other; while trading he stands with his
 * hands on his belt. Texture 64x64; cubes are tagged for tools/gen_textures_v8.py.
 */
public class DwarfModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, head, beard, cap, rightArm, leftArm, rightLeg, leftLeg;

    public DwarfModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.beard = head.getChild("beard");
        this.cap = head.getChild("cap");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-4.0F, 0.0F, -2.5F, 8, 8, 5), // tex:body
                PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -9.0F, -4.5F, 9, 9, 9), // tex:head
                PartPose.offset(0.0F, 10.0F, 0.0F));
        head.addOrReplaceChild("beard", CubeListBuilder.create()
                .texOffs(16, 31).addBox(-4.0F, 0.0F, -0.5F, 8, 6, 1) // tex:beard
                .texOffs(34, 31).addBox(-3.0F, 6.0F, -0.5F, 6, 3, 1) // tex:beard_low
                .texOffs(48, 31).addBox(-3.5F, 4.0F, -0.8F, 1, 5, 1) // tex:braid
                .texOffs(48, 31).addBox(2.5F, 4.0F, -0.8F, 1, 5, 1), // tex:braid
                PartPose.offset(0.0F, -3.0F, -5.0F));
        PartDefinition cap = head.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-5.0F, -3.0F, -5.0F, 10, 3, 10) // tex:cap
                .texOffs(16, 38).addBox(-5.0F, -0.5F, -6.0F, 10, 1, 2) // tex:visor
                .texOffs(52, 31).addBox(-1.0F, -2.5F, -5.6F, 2, 2, 1), // tex:lamp
                PartPose.offset(0.0F, -8.0F, 0.0F));

        for (int side = -1; side <= 1; side += 2) {
            root.addOrReplaceChild(side < 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(40, 18).addBox(-2.0F, -1.0F, -2.0F, 4, 8, 4), // tex:arm
                    PartPose.offset(side * 6.0F, 11.0F, 0.0F));
            root.addOrReplaceChild(side < 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(0, 31).addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4), // tex:leg
                    PartPose.offset(side * 2.0F, 18.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.9F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float trade = state.stance;

        float swing = Mth.cos(pos) * 0.8F * spd;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        rightArm.xRot = -swing * 0.8F + Mth.sin(t * 0.07F) * 0.03F;
        leftArm.xRot = swing * 0.8F - Mth.sin(t * 0.07F) * 0.03F;
        rightArm.yRot = leftArm.yRot = 0.0F;
        rightArm.zRot = 0.05F;
        leftArm.zRot = -0.05F;
        float bob = Math.abs(Mth.sin(pos)) * 0.6F * spd;
        body.y = head.y = 10.0F + bob;
        rightArm.y = leftArm.y = 11.0F + bob;

        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        beard.xRot = Math.max(0.0F, -head.xRot) * 0.4F + Mth.sin(t * 0.08F) * 0.03F + Math.abs(swing) * 0.08F;
        cap.xRot = 0.0F;
        cap.zRot = 0.0F;

        // Trading: hands on the belt.
        if (trade > 0.0F) {
            rightArm.xRot = Mth.lerp(trade, rightArm.xRot, -0.2F);
            leftArm.xRot = Mth.lerp(trade, leftArm.xRot, -0.2F);
            rightArm.zRot = Mth.lerp(trade, rightArm.zRot, 0.7F);
            leftArm.zRot = Mth.lerp(trade, leftArm.zRot, -0.7F);
            rightArm.yRot = 0.6F * trade;
            leftArm.yRot = -0.6F * trade;
        }

        // Idle work: straighten the cap (0 to 24), then stroke the beard (24 to 60).
        float a = state.ticksSince(state.actionA);
        if (a >= 0.0F && a < 60.0F) {
            float in = smooth(a / 6.0F) * (1.0F - smooth((a - 54.0F) / 6.0F));
            if (a < 24.0F) {
                rightArm.xRot = Mth.lerp(in, rightArm.xRot, -2.7F);
                rightArm.zRot = Mth.lerp(in, rightArm.zRot, 0.35F);
                float tug = a > 12.0F && a < 20.0F ? Mth.sin((a - 12.0F) / 8.0F * Mth.PI) : 0.0F;
                cap.zRot = -0.12F * tug;
                cap.xRot = -0.05F * tug;
                head.zRot = 0.08F * in;
            } else {
                float k = (a - 24.0F) * 0.35F;
                leftArm.xRot = Mth.lerp(in, leftArm.xRot, -1.35F + Mth.sin(k) * 0.2F);
                leftArm.zRot = Mth.lerp(in, leftArm.zRot, 0.45F);
                head.xRot = Mth.lerp(in, head.xRot, 0.15F);
                beard.xRot += 0.08F * Mth.sin(k) * in;
                head.zRot = 0.0F;
            }
        } else {
            head.zRot = 0.0F;
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }
}
