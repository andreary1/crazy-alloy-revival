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
 * Segway: two big wheels side by side (dark faceted tyres, two crossed slabs each, with light round hubs), a low
 * platform with light grip strips between them and a tall dark pole with a light stripe up the front, topped by a
 * T handlebar. No face, no rider. The wheels spin with its speed; platform and pole tip forward when it speeds up,
 * back when it brakes and sideways into turns; a ram jolts it forward.
 * Texture 64x64; cubes are tagged for tools/gen_textures_v7.py.
 */
public class SegwayModel extends EntityModel<RevivalRenderState> {
    private final ModelPart body, pole, rightWheel, leftWheel;

    public SegwayModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.pole = body.getChild("pole");
        this.rightWheel = root.getChild("right_wheel");
        this.leftWheel = root.getChild("left_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (int side = -1; side <= 1; side += 2) {
            float hubX = side < 0 ? -2.0F : 1.0F; // the hub sits on the outer face
            PartDefinition wheel = root.addOrReplaceChild(side < 0 ? "right_wheel" : "left_wheel", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1.0F, -5.0F, -2.0F, 2, 10, 4) // tex:tire_a
                    .texOffs(12, 0).addBox(-1.0F, -2.0F, -5.0F, 2, 4, 10) // tex:tire_b
                    .texOffs(36, 0).addBox(hubX, -2.0F, -2.0F, 1, 4, 4), // tex:hub
                    PartPose.offset(side * 6.0F, 19.0F, 0.0F));
            // The same two slabs turned 45 degrees: together they make an eight-sided tyre.
            wheel.addOrReplaceChild("facets", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1.0F, -5.0F, -2.0F, 2, 10, 4) // tex:tire_a
                    .texOffs(12, 0).addBox(-1.0F, -2.0F, -5.0F, 2, 4, 10), // tex:tire_b
                    PartPose.rotation(Mth.PI / 4.0F, 0.0F, 0.0F));
        }
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 14).addBox(-5.0F, -1.0F, -3.5F, 10, 2, 7), // tex:platform
                PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition pole = body.addOrReplaceChild("pole", CubeListBuilder.create()
                .texOffs(36, 14).addBox(-1.0F, -20.0F, -1.0F, 2, 20, 2), // tex:pole
                PartPose.offsetAndRotation(0.0F, -1.0F, 1.5F, -0.12F, 0.0F, 0.0F));
        pole.addOrReplaceChild("handlebar", CubeListBuilder.create()
                .texOffs(0, 36).addBox(-6.0F, -1.0F, -1.0F, 12, 2, 2), // tex:handlebar
                PartPose.offset(0.0F, -20.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        // Wheels roll with the distance travelled (radius 5 pixels).
        float spin = state.walkAnimationPos * 1.6F;
        rightWheel.xRot = spin;
        leftWheel.xRot = spin;
        float lean = state.stance;
        float roll = state.roll;
        body.xRot = lean;
        body.zRot = roll;
        pole.xRot = -0.12F + lean * 0.5F;
        // A tiny idle wobble: it balances itself.
        pole.zRot = Mth.sin(state.ageInTicks * 0.15F) * 0.015F;
        float a = state.attackAnim;
        if (a > 0.0F) {
            float p = Mth.sin(a * Mth.PI);
            body.xRot += 0.35F * p;
            body.z = -2.5F * p;
        } else {
            body.z = 0.0F;
        }
    }
}
