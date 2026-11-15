import com.dmzkittyclothes.client.pose.PoseSolver;
import com.dmzkittyclothes.client.pose.PoseSolver.Bone;
import com.dmzkittyclothes.client.pose.PoseSolver.PartPose;

import java.util.List;
import java.util.Random;

/**
 * Standalone check (no Minecraft needed) for PoseSolver.
 *
 * Truth: a point of the part, attached to the animated GeckoLib bone chain.
 * Result: the same point drawn through the vanilla ModelPart pose returned by PoseSolver.
 * Both must land on the same baked-space point. The old DragonMine Z VanillaModelSync formula is checked too,
 * to show that this test actually catches the bug.
 *
 * Run from the repo root:
 *   javac -d /tmp/pose-check src/main/java/com/dmzkittyclothes/client/pose/PoseSolver.java dev/PoseSolverCheck.java
 *   java -cp /tmp/pose-check PoseSolverCheck
 */
public class PoseSolverCheck {

    record Part(String name, double[] init, double[] bakedPivot, boolean underWaist) {
    }

    static final Part[] PARTS = {
            new Part("right_arm", new double[]{-5, 2, 0}, new double[]{5, 22, 0}, true),
            new Part("left_arm", new double[]{5, 2, 0}, new double[]{-5, 22, 0}, true),
            new Part("slim right_arm", new double[]{-5, 2.5, 0}, new double[]{4.75, 22, 0}, true),
            new Part("head", new double[]{0, 0, 0}, new double[]{0, 24, 0}, true),
            new Part("body", new double[]{0, 0, 0}, new double[]{0, 24, 0}, true),
            new Part("right_leg", new double[]{-1.9, 12, 0}, new double[]{1.9, 12, 0}, false),
            new Part("left_leg", new double[]{1.9, 12, 0}, new double[]{-1.9, 12, 0}, false),
    };

    public static void main(String[] args) {
        Random rnd = new Random(42);
        double worstNew = 0, worstOld = 0;
        int trials = 0;
        for (Part part : PARTS) {
            double newErr = 0, oldErr = 0;
            for (int t = 0; t < 2000; t++) {
                Bone waist = new Bone(r(rnd, 3), r(rnd, 3), r(rnd, 3), r(rnd, 1.2), r(rnd, 1.2), r(rnd, 1.2), 0, 12, 0);
                Bone bone = new Bone(r(rnd, 4), r(rnd, 4), r(rnd, 4), r(rnd, 3.1), r(rnd, 1.5), r(rnd, 3.1),
                        part.bakedPivot()[0], part.bakedPivot()[1], part.bakedPivot()[2]);
                List<Bone> chain = part.underWaist() ? List.of(waist, bone) : List.of(bone);
                double[] local = {r(rnd, 4), r(rnd, 8), r(rnd, 3)};

                double[] vanillaAbs = {part.init()[0] + local[0], part.init()[1] + local[1], part.init()[2] + local[2]};
                double[] truth = PoseSolver.applyChain(chain, PoseSolver.vanillaToBaked(vanillaAbs));

                PartPose fixed = PoseSolver.solve(chain, part.init()[0], part.init()[1], part.init()[2]);
                double[] got = PoseSolver.vanillaToBaked(PoseSolver.applyPart(fixed, local));
                newErr = Math.max(newErr, dist(truth, got));

                PartPose old = oldDmzSync(bone, part.underWaist() ? waist : null, part.init());
                double[] gotOld = PoseSolver.vanillaToBaked(PoseSolver.applyPart(old, local));
                oldErr = Math.max(oldErr, dist(truth, gotOld));
                trials++;
            }
            System.out.printf("%-15s  novo (PoseSolver): erro max %.2e px   |   formula antiga do DMZ: erro max %.2f px%n",
                    part.name(), newErr, oldErr);
            worstNew = Math.max(worstNew, newErr);
            worstOld = Math.max(worstOld, oldErr);
        }

        // gimbal lock: yRot exactly +-90 degrees
        for (double y : new double[]{Math.PI / 2, -Math.PI / 2}) {
            Bone arm = new Bone(0, 0, 0, 0.7, y, -0.4, 5, 22, 0);
            List<Bone> chain = List.of(arm);
            double[] local = {1, 5, 1};
            double[] truth = PoseSolver.applyChain(chain, PoseSolver.vanillaToBaked(new double[]{-5 + 1, 2 + 5, 1}));
            PartPose p = PoseSolver.solve(chain, -5, 2, 0);
            double e = dist(truth, PoseSolver.vanillaToBaked(PoseSolver.applyPart(p, local)));
            System.out.printf("gimbal yRot=%+.0f deg: erro %.2e px%n", Math.toDegrees(y), e);
            worstNew = Math.max(worstNew, e);
        }

        System.out.printf("%n%d casos. Pior erro novo: %.2e px. Pior erro da formula antiga: %.2f px.%n", trials, worstNew, worstOld);
        if (worstNew > 1e-9) {
            System.out.println("FALHOU");
            System.exit(1);
        }
        System.out.println("OK");
    }

    /** Copy of com.dragonminez.client.render.VanillaModelSync.syncBone (DMZ 2.2), for comparison only. */
    static PartPose oldDmzSync(Bone bone, Bone parent, double[] init) {
        double prx = 0, pry = 0, prz = 0, ppx = 0, ppy = 0, ppz = 0, pvx = 0, pvy = 0, pvz = 0;
        if (parent != null) {
            prx = parent.rotX(); pry = parent.rotY(); prz = parent.rotZ();
            ppx = parent.posX(); ppy = parent.posY(); ppz = parent.posZ();
            pvx = parent.pivX(); pvy = parent.pivY(); pvz = parent.pivZ();
        }
        double xr = -(prx + bone.rotX()), yr = -(pry + bone.rotY()), zr = (prz + bone.rotZ());
        double relX = bone.pivX() + bone.posX() - pvx;
        double relY = bone.pivY() + bone.posY() - pvy;
        double relZ = bone.pivZ() + bone.posZ() - pvz;
        double[] rot = rotateZYX(relX, relY, relZ, prx, pry, prz);
        double dx = (pvx + ppx + rot[0]) - bone.pivX();
        double dy = (pvy + ppy + rot[1]) - bone.pivY();
        double dz = (pvz + ppz + rot[2]) - bone.pivZ();
        return new PartPose(init[0] - dx, init[1] - dy, init[2] + dz, xr, yr, zr);
    }

    static double[] rotateZYX(double x, double y, double z, double rx, double ry, double rz) {
        double cx = Math.cos(rx), sx = Math.sin(rx);
        double y1 = y * cx - z * sx, z1 = y * sx + z * cx;
        double cy = Math.cos(ry), sy = Math.sin(ry);
        double x2 = x * cy + z1 * sy, z2 = -x * sy + z1 * cy;
        double cz = Math.cos(rz), sz = Math.sin(rz);
        return new double[]{x2 * cz - y1 * sz, x2 * sz + y1 * cz, z2};
    }

    static double r(Random rnd, double range) {
        return (rnd.nextDouble() * 2 - 1) * range;
    }

    static double dist(double[] a, double[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2));
    }
}
