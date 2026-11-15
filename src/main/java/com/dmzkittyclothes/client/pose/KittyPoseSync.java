package com.dmzkittyclothes.client.pose;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.List;

/**
 * Re-syncs the vanilla player model that DragonMine Z feeds to third-party layers (Curios), using the exact bone
 * chain instead of DMZ's approximation. See {@link PoseSolver} for the reason.
 * <p>
 * Runs right after {@code VanillaModelSync.sync}, so everything DMZ sets besides the six poses stays as is.
 * <p>
 * To compare in game: start with {@code -Ddmzkitty.exactPose=false} to get DMZ's original behavior back.
 */
public final class KittyPoseSync {

    private static final boolean ENABLED = !"false".equalsIgnoreCase(System.getProperty("dmzkitty.exactPose"));

    private KittyPoseSync() {
    }

    public static void apply(BakedGeoModel geoModel, PlayerModel<AbstractClientPlayer> vanillaModel) {
        if (!ENABLED || geoModel == null) return;

        syncPart(geoModel, "head", vanillaModel.head);
        syncPart(geoModel, "head", vanillaModel.hat);
        syncPart(geoModel, "body", vanillaModel.body);
        syncPart(geoModel, "right_arm", vanillaModel.rightArm);
        syncPart(geoModel, "left_arm", vanillaModel.leftArm);
        syncPart(geoModel, "right_leg", vanillaModel.rightLeg);
        syncPart(geoModel, "left_leg", vanillaModel.leftLeg);
    }

    private static void syncPart(BakedGeoModel geoModel, String boneName, ModelPart part) {
        GeoBone bone = geoModel.getBone(boneName).orElse(null);
        if (bone == null) return;

        // Bones from the one below "root" down to this bone. "root" is already applied by GeckoLib to the PoseStack
        // when DMZ's forwarder runs (it is called from root's renderForBone).
        List<PoseSolver.Bone> chain = new ArrayList<>(3);
        for (GeoBone b = bone; b != null && !"root".equals(b.getName()); b = b.getParent()) {
            chain.add(0, new PoseSolver.Bone(
                    b.getPosX(), b.getPosY(), b.getPosZ(),
                    b.getRotX(), b.getRotY(), b.getRotZ(),
                    b.getPivotX(), b.getPivotY(), b.getPivotZ()));
        }
        if (chain.isEmpty()) return;

        net.minecraft.client.model.geom.PartPose init = part.getInitialPose();
        PoseSolver.PartPose pose = PoseSolver.solve(chain, init.x, init.y, init.z);
        if (!finite(pose)) return;

        part.x = (float) pose.x();
        part.y = (float) pose.y();
        part.z = (float) pose.z();
        part.xRot = (float) pose.xRot();
        part.yRot = (float) pose.yRot();
        part.zRot = (float) pose.zRot();
    }

    private static boolean finite(PoseSolver.PartPose p) {
        return Double.isFinite(p.x()) && Double.isFinite(p.y()) && Double.isFinite(p.z())
                && Double.isFinite(p.xRot()) && Double.isFinite(p.yRot()) && Double.isFinite(p.zRot());
    }
}
