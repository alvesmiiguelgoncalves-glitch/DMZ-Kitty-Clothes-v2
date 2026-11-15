package com.dmzkittyclothes.client.pose;

import java.util.List;

/**
 * Exact conversion from a GeckoLib bone chain to the pose of a vanilla {@code ModelPart}.
 * <p>
 * Pure math, no Minecraft classes, so it can be checked with {@code dev/PoseSolverCheck.java}.
 * <p>
 * Why this exists: DragonMine Z's {@code VanillaModelSync} adds the parent's rotation to the child's rotation
 * (only valid when both turn on the same axis) and flips the sign of the bone's X position. Third-party layers
 * (Curios) then draw on a pose that does not match the animated body. Here the whole bone chain is composed as
 * matrices, exactly like GeckoLib's {@code RenderUtils.prepMatrixForBone}, and the result is turned back into
 * {@code x, y, z, xRot, yRot, zRot} for the vanilla part.
 * <p>
 * Frames: GeckoLib works in "baked" pixels (x mirrored, y up, feet at 0). The vanilla layer is drawn after
 * {@code scale(-1, -1, 1)} and a 24 px lift, so a vanilla point (x, y, z) is the baked point (-x, 24 - y, z).
 * That mapping is {@link #F}, and it is its own inverse.
 */
public final class PoseSolver {

    private PoseSolver() {
    }

    /** Animated state of one GeckoLib bone: position (px), rotation (rad), pivot (px, baked). */
    public record Bone(double posX, double posY, double posZ,
                       double rotX, double rotY, double rotZ,
                       double pivX, double pivY, double pivZ) {
    }

    /** Vanilla part pose: position in px and rotation in rad (applied as Rz * Ry * Rx, like ModelPart). */
    public record PartPose(double x, double y, double z, double xRot, double yRot, double zRot) {
    }

    // vanilla px -> baked px: (x, y, z) -> (-x, 24 - y, z). Row-major 4x4.
    private static final double[] F = {
            -1, 0, 0, 0,
            0, -1, 0, 24,
            0, 0, 1, 0,
            0, 0, 0, 1};

    /**
     * @param chain bones from the outermost ancestor (below the root, which is already on the PoseStack) down to
     *              the bone itself. Example for an arm: [waist, right_arm]. For a leg: [right_leg].
     * @param initX initial vanilla part position (what {@code ModelPart.getInitialPose()} returns)
     */
    public static PartPose solve(List<Bone> chain, double initX, double initY, double initZ) {
        double[] g = identity();
        for (Bone b : chain) g = mul(g, boneMatrix(b));

        // vanilla local point l  ->  vanilla absolute (init + l)  ->  baked  ->  animate with g  ->  back to vanilla
        double[] a = mul(mul(mul(F, g), F), translation(initX, initY, initZ));

        double r20 = a[8];
        double yRot = Math.asin(clamp(-r20));
        double xRot;
        double zRot;
        if (Math.abs(r20) < 0.999999) {
            xRot = Math.atan2(a[9], a[10]);
            zRot = Math.atan2(a[4], a[0]);
        } else {
            // gimbal lock (looking straight up/down on Y): any x + z split is the same matrix, keep z = 0
            zRot = 0;
            xRot = Math.atan2(-a[6], a[5]);
        }
        return new PartPose(a[3], a[7], a[11], xRot, yRot, zRot);
    }

    /** GeckoLib prepMatrixForBone: T(-posX, posY, posZ) * T(pivot) * Rz * Ry * Rx * T(-pivot). Scale is not applied. */
    static double[] boneMatrix(Bone b) {
        double[] m = translation(-b.posX(), b.posY(), b.posZ());
        m = mul(m, translation(b.pivX(), b.pivY(), b.pivZ()));
        if (b.rotZ() != 0) m = mul(m, rotZ(b.rotZ()));
        if (b.rotY() != 0) m = mul(m, rotY(b.rotY()));
        if (b.rotX() != 0) m = mul(m, rotX(b.rotX()));
        return mul(m, translation(-b.pivX(), -b.pivY(), -b.pivZ()));
    }

    /** Applies the bone chain to a baked-space point. Used by the check program. */
    public static double[] applyChain(List<Bone> chain, double[] point) {
        double[] g = identity();
        for (Bone b : chain) g = mul(g, boneMatrix(b));
        return transform(g, point);
    }

    /** Vanilla part transform (translate, then Rz * Ry * Rx) applied to a part-local point. Used by the check program. */
    public static double[] applyPart(PartPose p, double[] local) {
        double[] m = translation(p.x(), p.y(), p.z());
        m = mul(m, rotZ(p.zRot()));
        m = mul(m, rotY(p.yRot()));
        m = mul(m, rotX(p.xRot()));
        return transform(m, local);
    }

    /** Vanilla px point to baked px point. */
    public static double[] vanillaToBaked(double[] p) {
        return transform(F, p);
    }

    // ---- small 4x4 matrix helpers (row-major) ----

    private static double clamp(double v) {
        return v < -1 ? -1 : Math.min(v, 1);
    }

    private static double[] identity() {
        return new double[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    }

    private static double[] translation(double x, double y, double z) {
        return new double[]{1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1};
    }

    private static double[] rotX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[]{1, 0, 0, 0, 0, c, -s, 0, 0, s, c, 0, 0, 0, 0, 1};
    }

    private static double[] rotY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[]{c, 0, s, 0, 0, 1, 0, 0, -s, 0, c, 0, 0, 0, 0, 1};
    }

    private static double[] rotZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[]{c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    }

    private static double[] mul(double[] a, double[] b) {
        double[] r = new double[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double s = 0;
                for (int k = 0; k < 4; k++) s += a[i * 4 + k] * b[k * 4 + j];
                r[i * 4 + j] = s;
            }
        }
        return r;
    }

    private static double[] transform(double[] m, double[] p) {
        return new double[]{
                m[0] * p[0] + m[1] * p[1] + m[2] * p[2] + m[3],
                m[4] * p[0] + m[5] * p[1] + m[6] * p[2] + m[7],
                m[8] * p[0] + m[9] * p[1] + m[10] * p[2] + m[11]};
    }
}
