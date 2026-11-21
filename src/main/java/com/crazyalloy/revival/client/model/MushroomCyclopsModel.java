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
 * Mushroom Cyclops: a bulkier humanoid. A wide head with one big square light yellow eye (also drawn by the glow
 * layer) over a dark mouth line, under a roughly rectangular, nearly black dark blue cap with small light spots and a
 * stepped top. The torso is long and a little wider at the chest than at the waist; the thick arms are bent and held
 * out from the body, in two segments, ending in dark fists; the legs are straight and thick.
 * <p>
 * Animations (revival proposal): heavy, slow steps with the body rolling from side to side, the arms swinging a
 * little; a forward lean while hunting; the slam (action A): both fists rise over the head during the wind-up, then
 * come down hard in front. Texture 128x64; cubes are tagged for tools/gen_textures_v9.py.
 */
public class MushroomCyclopsModel extends EntityModel<RevivalRenderState> {
    /** Ticks of the wind-up (matches MushroomCyclops.WINDUP_TICKS) and of the strike after it. */
    private static final float WINDUP = 16.0F, STRIKE = 8.0F;
    private static final float UPPER_X = -0.15F, FOREARM_X = -0.6F, ARM_Z = 0.3F;

    private final ModelPart waist, chest, head, rightLeg, leftLeg;
    private final ModelPart[] upper = new ModelPart[2], forearm = new ModelPart[2];

    public MushroomCyclopsModel(ModelPart root) {
        super(root);
        this.waist = root.getChild("waist");
        this.chest = waist.getChild("chest");
        this.head = chest.getChild("head");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        for (int i = 0; i < 2; i++) {
            this.upper[i] = chest.getChild(i == 0 ? "right_arm" : "left_arm");
            this.forearm[i] = upper[i].getChild("forearm");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create()
                .texOffs(32, 17).addBox(-4.0F, -5.0F, -2.5F, 8, 5, 5), // tex:waist
                PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition chest = waist.addOrReplaceChild("chest", CubeListBuilder.create()
                .texOffs(0, 17).addBox(-5.0F, -7.0F, -3.0F, 10, 7, 6), // tex:chest
                PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(84, 0).addBox(-4.0F, -7.0F, -3.5F, 8, 7, 7) // tex:head
                .texOffs(114, 0).addBox(-2.0F, -5.5F, -3.8F, 4, 4, 1) // tex:eye
                .texOffs(114, 5).addBox(-2.0F, -1.2F, -3.6F, 4, 1, 1), // tex:mouth
                PartPose.offset(0.0F, -7.0F, 0.0F));
        head.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.0F, -4.0F, -5.5F, 12, 5, 11) // tex:cap
                .texOffs(46, 0).addBox(-5.0F, -6.0F, -4.5F, 10, 2, 9), // tex:cap_top
                PartPose.offset(0.0F, -7.0F, 0.0F));
        for (int i = 0; i < 2; i++) {
            float side = i == 0 ? -1.0F : 1.0F;
            PartDefinition arm = chest.addOrReplaceChild(i == 0 ? "right_arm" : "left_arm", CubeListBuilder.create()
                    .texOffs(58, 17).addBox(-2.0F, -1.0F, -2.0F, 4, 7, 4), // tex:upper_arm
                    PartPose.offsetAndRotation(side * 7.0F, -6.0F, 0.0F, UPPER_X, 0.0F, side * ARM_Z));
            PartDefinition fore = arm.addOrReplaceChild("forearm", CubeListBuilder.create()
                    .texOffs(74, 17).addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4), // tex:forearm
                    PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, FOREARM_X, 0.0F, -side * 0.15F));
            fore.addOrReplaceChild("fist", CubeListBuilder.create()
                    .texOffs(90, 17).addBox(-2.0F, 0.0F, -2.0F, 4, 3, 4), // tex:fist
                    PartPose.offset(0.0F, 7.0F, 0.0F));
            root.addOrReplaceChild(i == 0 ? "right_leg" : "left_leg", CubeListBuilder.create()
                    .texOffs(0, 30).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4), // tex:leg
                    PartPose.offset(side * 2.5F, 12.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float pos = state.walkAnimationPos * 0.5F;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        float hunt = state.stance;

        float swing = Mth.cos(pos) * 0.6F * spd;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        // Heavy steps: the body sinks and rolls toward the leg that carries it.
        waist.y = 12.0F + Math.abs(Mth.sin(pos)) * 0.8F * spd;
        waist.zRot = Mth.sin(pos) * 0.07F * spd;
        waist.xRot = 0.0F;
        chest.xRot = 0.18F * hunt + Mth.sin(t * 0.05F) * 0.015F;
        head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.8F;
        head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.6F - 0.12F * hunt;
        for (int i = 0; i < 2; i++) {
            float side = i == 0 ? -1.0F : 1.0F;
            upper[i].xRot = UPPER_X - (i == 0 ? -swing : swing) * 0.4F - 0.3F * hunt;
            upper[i].zRot = side * ARM_Z;
            forearm[i].xRot = FOREARM_X - 0.3F * hunt;
        }

        // Slam: fists up over the head during the wind-up, then down in front.
        float a = state.ticksSince(state.actionA);
        if (a >= 0.0F && a < WINDUP + STRIKE) {
            float up, down;
            if (a < WINDUP) {
                up = smooth(a / (WINDUP * 0.7F));
                down = 0.0F;
            } else {
                float s = (a - WINDUP) / STRIKE;
                up = 1.0F - smooth(s * 2.0F);
                down = Mth.sin(Math.min(1.0F, s * 1.3F) * Mth.PI);
            }
            for (int i = 0; i < 2; i++) {
                float side = i == 0 ? -1.0F : 1.0F;
                upper[i].xRot = Mth.lerp(up, upper[i].xRot, -2.9F) + down * 0.5F;
                upper[i].zRot = Mth.lerp(up, upper[i].zRot, side * 0.12F);
                forearm[i].xRot = Mth.lerp(up, forearm[i].xRot, -0.4F) - down * 0.2F;
            }
            chest.xRot += -0.25F * up + 0.45F * down;
            head.xRot += -0.15F * up + 0.2F * down;
            waist.y += 1.2F * down;
        } else if (state.attackAnim > 0.0F) {
            float k = Mth.sin(state.attackAnim * Mth.PI);
            for (int i = 0; i < 2; i++) {
                upper[i].xRot -= 1.4F * k;
            }
            chest.xRot += 0.25F * k;
        }
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }
}
