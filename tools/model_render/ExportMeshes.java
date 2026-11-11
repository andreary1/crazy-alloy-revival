// Bakes each mob model (rest pose after setupAnim with a default render state) and dumps its quads as JSON:
// per vertex [x, y, z, u, v, nx, ny, nz] in model space. Compile and run against the mod classpath, then feed render.py.
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.crazyalloy.revival.client.model.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public class ExportMeshes {
    static class Rec implements VertexConsumer {
        final StringBuilder sb = new StringBuilder(); int n = 0;
        public VertexConsumer addVertex(float x, float y, float z) { if (n++ > 0) sb.append(','); sb.append('[').append(x).append(',').append(y).append(',').append(z); return this; }
        public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
        public VertexConsumer setColor(int c) { return this; }
        public VertexConsumer setUv(float u, float v) { sb.append(',').append(u).append(',').append(v); return this; }
        public VertexConsumer setUv1(int u, int v) { return this; }
        public VertexConsumer setUv2(int u, int v) { return this; }
        public VertexConsumer setNormal(float x, float y, float z) { sb.append(',').append(x).append(',').append(y).append(',').append(z).append(']'); return this; }
        public VertexConsumer setLineWidth(float w) { return this; }
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    static void dump(String name, Supplier<LayerDefinition> layer, Function<ModelPart, EntityModel> ctor, Supplier<? extends EntityRenderState> st, File out) throws Exception {
        ModelPart root = layer.get().bakeRoot();
        try {
            EntityModel m = ctor.apply(root);
            EntityRenderState s = st.get();
            m.setupAnim(s);
        } catch (Throwable t) { System.err.println(name + ": setupAnim skipped: " + t); root.resetPose(); }
        Rec r = new Rec();
        root.render(new PoseStack(), r, 0xF000F0, 0);
        try (Writer w = new FileWriter(new File(out, name + ".json"))) { w.write("[" + r.sb + "]"); }
        System.out.println(name + " verts=" + r.n);
    }
    public static void main(String[] a) throws Exception {
        File out = new File(a[0]); out.mkdirs();
        dump("candy_tube_dog", CandyTubeDogModel::createBodyLayer, CandyTubeDogModel::new, CandyTubeDogRenderState::new, out);
        dump("lollipop_guy", LollipopGuyModel::createBodyLayer, LollipopGuyModel::new, LollipopGuyRenderState::new, out);
        dump("grape_spider", GrapeSpiderModel::createBodyLayer, GrapeSpiderModel::new, RevivalRenderState::new, out);
        dump("brown_sugar_rhino", BrownSugarRhinoModel::createBodyLayer, BrownSugarRhinoModel::new, RevivalRenderState::new, out);
        dump("cotton_candy_tornado", CottonCandyTornadoModel::createBodyLayer, CottonCandyTornadoModel::new, RevivalRenderState::new, out);
        dump("gingerbread_warrior", GingerbreadModel::createWarriorLayer, p -> new GingerbreadModel(p, GingerbreadModel.Kind.WARRIOR), RevivalRenderState::new, out);
        dump("gingerbread_soldier", GingerbreadModel::createSoldierLayer, p -> new GingerbreadModel(p, GingerbreadModel.Kind.SOLDIER), RevivalRenderState::new, out);
        dump("gingerbread_king", GingerbreadKingModel::createBodyLayer, GingerbreadKingModel::new, RevivalRenderState::new, out);
        dump("jelly_shark", JellySharkModel::createBodyLayer, JellySharkModel::new, RevivalRenderState::new, out);
        dump("roll_cake_monster", RollCakeMonsterModel::createBodyLayer, RollCakeMonsterModel::new, RevivalRenderState::new, out);
        dump("ice_cream_vendor", IceCreamVendorModel::createBodyLayer, IceCreamVendorModel::new, RevivalRenderState::new, out);
        dump("impostor_cake", ImpostorCakeModel::createBodyLayer, ImpostorCakeModel::new, RevivalRenderState::new, out);
        dump("ice_cream_zombie", IceCreamZombieModel::createBodyLayer, IceCreamZombieModel::new, RevivalRenderState::new, out);
        dump("ice_cream_beast", IceCreamBeastModel::createBodyLayer, IceCreamBeastModel::new, RevivalRenderState::new, out);
        dump("ice_cream_gargoyle", IceCreamGargoyleModel::createBodyLayer, IceCreamGargoyleModel::new, RevivalRenderState::new, out);
        dump("living_ice_cream", LivingIceCreamModel::createBodyLayer, LivingIceCreamModel::new, RevivalRenderState::new, out);
        dump("angry_ice_cream_cone", AngryIceCreamConeModel::createBodyLayer, AngryIceCreamConeModel::new, RevivalRenderState::new, out);
        dump("ice_cream_dragon", IceCreamDragonModel::createBodyLayer, IceCreamDragonModel::new, RevivalRenderState::new, out);
        dump("jelly_bunny", JellyBunnyModel::createBodyLayer, JellyBunnyModel::new, net.minecraft.client.renderer.entity.state.LivingEntityRenderState::new, out);
        dump("jelly_snake", JellySnakeModel::createBodyLayer, JellySnakeModel::new, net.minecraft.client.renderer.entity.state.LivingEntityRenderState::new, out);
        dump("bubblegum", BubblegumModel::createBodyLayer, BubblegumModel::new, net.minecraft.client.renderer.entity.state.LivingEntityRenderState::new, out);
    }
}
