package com.crazyalloy.revival.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Shadow Eye: a purple, roughly cubic core with a stepped top, one big orange eye with a vertical dark pupil on its
 * front, and three thin dark purple rings around it at different tilts (each an octagon of thin segments), leaving
 * gaps where the background shows through.
 * <p>
 * Animations (revival proposal): the rings turn slowly, each at its own speed; the core bobs gently up and down; the
 * pupil drifts toward where it looks. During the gaze warning (stance 0 to 1) the rings swing round to line up
 * around the eye and spin faster, the pupil widens and the core stops bobbing; action A gives a short jolt at the
 * start of the warning. Texture 64x64; cubes are tagged for tools/gen_textures_v8.py.
 */
public class ShadowEyeModel extends EntityModel<RevivalRenderState> {
    private static final int SEGMENTS = 8;
    private static final float RADIUS = 9.0F;
    /** Rest tilts (x, y, z) of the three rings: one in front leaning back, one round the sides, one above. */
    private static final float[][] TILTS = {{0.35F, 0.0F, 0.25F}, {0.2F, 1.35F, -0.3F}, {1.25F, 0.3F, 0.15F}};
    private static final float[] SPIN = {0.035F, -0.025F, 0.045F};

    private final ModelPart core, pupil;
    private final ModelPart[] tilt = new ModelPart[3], spin = new ModelPart[3];

    public ShadowEyeModel(ModelPart root) {
        super(root);
        this.core = root.getChild("core");
        this.pupil = core.getChild("pupil");
        for (int i = 0; i < 3; i++) {
            this.tilt[i] = core.getChild("ring" + i);
            this.spin[i] = tilt[i].getChild("spin");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition core = root.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10, 10, 10) // tex:core
                .texOffs(0, 20).addBox(-4.0F, -6.0F, -4.0F, 8, 1, 8) // tex:step
                .texOffs(34, 20).addBox(-2.5F, -7.0F, -2.5F, 5, 1, 5) // tex:crest
                .texOffs(0, 30).addBox(-3.5F, -3.5F, -5.5F, 7, 7, 1), // tex:iris
                PartPose.offset(0.0F, 17.0F, 0.0F));
        core.addOrReplaceChild("pupil", CubeListBuilder.create()
                .texOffs(18, 30).addBox(-0.5F, -2.0F, -0.5F, 1, 4, 1), // tex:pupil
                PartPose.offset(0.0F, 0.0F, -5.7F));

        for (int i = 0; i < 3; i++) {
            PartDefinition ring = core.addOrReplaceChild("ring" + i, CubeListBuilder.create(),
                    PartPose.rotation(TILTS[i][0], TILTS[i][1], TILTS[i][2]));
            PartDefinition spin = ring.addOrReplaceChild("spin", CubeListBuilder.create(), PartPose.ZERO);
            for (int s = 0; s < SEGMENTS; s++) {
                float a = s * Mth.TWO_PI / SEGMENTS;
                spin.addOrReplaceChild("seg" + s, CubeListBuilder.create()
                        .texOffs(24, 30).addBox(-4.0F, -0.5F, -0.5F, 8, 1, 1, new CubeDeformation(-0.3F)), // tex:ring
                        PartPose.offsetAndRotation(Mth.cos(a) * RADIUS, Mth.sin(a) * RADIUS, 0.0F, 0.0F, 0.0F, a + Mth.HALF_PI));
            }
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RevivalRenderState state) {
        super.setupAnim(state);
        float t = state.ageInTicks;
        float c = state.stance;

        // Gentle bob and sway, stilled during the warning.
        core.y = 17.0F + Mth.sin(t * 0.1F) * 1.2F * (1.0F - c);
        core.yRot = state.yRot * Mth.DEG_TO_RAD * 0.3F;
        core.xRot = Mth.sin(t * 0.05F) * 0.05F * (1.0F - c);
        core.zRot = Mth.cos(t * 0.07F) * 0.04F * (1.0F - c);

        // The pupil drifts toward where the eye looks and widens during the warning.
        pupil.x = Mth.clamp(state.yRot / 45.0F, -1.0F, 1.0F) * 1.8F;
        pupil.y = Mth.clamp(state.xRot / 45.0F, -1.0F, 1.0F) * 1.2F;
        pupil.xScale = 1.0F + 2.2F * c;
        pupil.yScale = 1.0F + 0.25F * c;

        // Rings: slow turns at rest; during the warning they line up round the eye and spin faster.
        for (int i = 0; i < 3; i++) {
            float aligned = i * 0.2F;
            tilt[i].xRot = Mth.lerp(c, TILTS[i][0], 0.0F);
            tilt[i].yRot = Mth.lerp(c, TILTS[i][1], 0.0F);
            tilt[i].zRot = Mth.lerp(c, TILTS[i][2], aligned);
            spin[i].zRot = t * SPIN[i] * (1.0F + 4.0F * c);
            float scale = 1.0F - 0.12F * i * c;
            spin[i].xScale = spin[i].yScale = scale;
        }

        // Action A: a short jolt as the warning begins.
        float a = state.ticksSince(state.actionA);
        if (a >= 0.0F && a < 6.0F) {
            float jolt = Mth.sin(a / 6.0F * Mth.PI);
            core.xScale = core.yScale = core.zScale = 1.0F + 0.12F * jolt;
        } else {
            core.xScale = core.yScale = core.zScale = 1.0F;
        }
    }
}
