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
 * Mushroomcopter: a wide, roughly cubic purple cap with big irregular light spots, a small dark grey face under it
 * with two light, slanted eyes (also drawn by the glow layer), and a short rod on top carrying a horizontal propeller
 * of two crossed blades on a hub. No arms or legs.
 * <p>
 * Animations (revival proposal): the propeller spins all the time (its angle comes from Mushroomcopter#roll); the
 * body bobs gently and sways as it flies; while aiming (stance) the body tips forward, the propeller spins faster and
 * the eyes narrow a little. Texture 64x64; cubes are tagged for tools/gen_textures_v9.py.
 */
public class MushroomcopterModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, propeller, rightEye, leftEye;

    public MushroomcopterModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.propeller = body.getChild("propeller");
        ModelPart face = body.getChild("face");
        this.rightEye = face.getChild("right_eye");
        this.leftEye = face.getChild("left_eye");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10, 7, 10) // tex:cap
                .texOffs(30, 17).addBox(-0.5F, -11.0F, -0.5F, 1, 4, 1), // tex:rod
                PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition face = body.addOrReplaceChild("face", CubeListBuilder.create()
                .texOffs(0, 17).addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6), // tex:face
                PartPose.offset(0.0F, 0.0F, 0.0F));
        face.addOrReplaceChild("right_eye", CubeListBuilder.create()
                .texOffs(24, 17).addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1), // tex:eye
                PartPose.offsetAndRotation(-1.5F, 1.6F, -2.85F, 0.0F, 0.0F, -0.4F));
        face.addOrReplaceChild("left_eye", CubeListBuilder.create()
                .texOffs(24, 17).addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1), // tex:eye
                PartPose.offsetAndRotation(1.5F, 1.6F, -2.85F, 0.0F, 0.0F, 0.4F));
        PartDefinition propeller = body.addOrReplaceChild("propeller", CubeListBuilder.create()
                .texOffs(36, 17).addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2) // tex:hub
                .texOffs(0, 27).addBox(-7.0F, -1.5F, -1.0F, 14, 1, 2), // tex:blade
                PartPose.offset(0.0F, -11.0F, 0.0F));
        propeller.addOrReplaceChild("cross_blade", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-7.0F, -1.5F, -1.0F, 14, 1, 2), // tex:blade
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, Mth.HALF_PI, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float aim = state.stance;
        float spd = Math.min(1.0F, state.walkAnimationSpeed);
        body.y = 16.0F + Mth.sin(t * 0.15F) * 0.6F;
        body.xRot = 0.15F * spd + 0.35F * aim;
        body.zRot = Mth.sin(t * 0.09F) * 0.05F;
        body.yRot = state.yRot * Mth.DEG_TO_RAD * 0.3F;
        propeller.yRot = state.roll;
        // Narrowed, angrier eyes while aiming.
        rightEye.zRot = -0.4F - 0.15F * aim;
        leftEye.zRot = 0.4F + 0.15F * aim;
    }
}
