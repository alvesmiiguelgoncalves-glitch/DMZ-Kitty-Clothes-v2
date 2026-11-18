package com.dmzkittyclothes.client;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Mola amortecida (criticamente amortecida: nao fica "agitada") para a ponta do cachecol.
 * Alvo X (para tras): sobe com a velocidade para frente e com a queda. Alvo Z (lado): contrario ao movimento lateral.
 * Estado por entidade; o tempo vem de tickCount + partialTick, entao chamar varias vezes no mesmo quadro nao avanca a mola.
 */
public final class ScarfPhysics {

    private static final class State {
        float ax, az, vx, vz, fx, fz, fvx, fvz, time = Float.NaN;
    }

    private static final Map<LivingEntity, State> STATES = new WeakHashMap<>();

    private ScarfPhysics() {
    }

    /** @return {xRot, zRot} da ponta de tras e {xRot, zRot} da ponta da frente, em radianos. */
    public static float[] update(LivingEntity e, float partial, float swing, float stiffness, boolean charging) {
        State s = STATES.computeIfAbsent(e, k -> new State());
        float now = (e.tickCount + partial) / 20F;
        float dt = Float.isNaN(s.time) ? 0F : now - s.time;
        if (dt < 0F || dt > 0.3F) {
            dt = 0F; // reset (teleporte, mundo recarregado, entidade fora de vista)
            s.vx = 0F;
            s.vz = 0F;
            s.fvx = 0F;
            s.fvz = 0F;
        }
        s.time = now;

        // velocidade em blocos/tick no referencial do corpo
        double vx = e.getX() - e.xo, vy = e.getY() - e.yo, vz = e.getZ() - e.zo;
        double yaw = Math.toRadians(e.yBodyRot);
        double forward = vx * -Math.sin(yaw) + vz * Math.cos(yaw);
        double side = vx * Math.cos(yaw) + vz * Math.sin(yaw); // + = para a esquerda do jogador

        float targetX = 3F + clamp((float) (forward * 90D), 0F, 40F) + clamp((float) (-vy * 35D), 0F, 30F)
                + (float) Math.sin(now * 1.3F) * 2F;
        float targetZ = clamp((float) (side * 120D), -25F, 25F);
        // ponta da frente: encosta no corpo quando corre pra frente; vai pra frente (xRot < 0) ao andar de re ou subir
        float frontX = -(2F + clamp((float) (-forward * 140D), 0F, 40F) + clamp((float) (vy * 35D), 0F, 25F))
                - (float) Math.sin(now * 1.1F + 1.5F) * 1.5F;
        frontX = Math.min(frontX, 0F);
        float targetZ0 = targetZ;
        if (charging) {
            // carregando ki: o vento da aura levanta e agita as pontas
            targetX = 40F + (float) Math.sin(now * 6F) * 15F;
            targetZ0 = (float) Math.sin(now * 4.7F) * 14F;
            frontX = -(28F + (float) Math.sin(now * 5.3F + 1F) * 12F);
        }
        targetZ = targetZ0;
        targetX *= swing;
        targetZ *= swing;
        frontX *= swing;
        float frontZ = targetZ;
        if (swing <= 0F) {
            targetX = 0F;
            targetZ = 0F;
            frontX = 0F;
            frontZ = 0F;
        }

        float c = 2F * (float) Math.sqrt(stiffness); // amortecimento critico
        // integracao semi-implicita em passos pequenos (estavel mesmo com quadros lentos)
        int steps = Math.max(1, Math.round(dt / 0.01F));
        float h = dt / steps;
        for (int i = 0; i < steps && dt > 0F; i++) {
            s.vx += (stiffness * (targetX - s.ax) - c * s.vx) * h;
            s.ax += s.vx * h;
            s.vz += (stiffness * (targetZ - s.az) - c * s.vz) * h;
            s.az += s.vz * h;
            s.fvx += (stiffness * (frontX - s.fx) - c * s.fvx) * h;
            s.fx += s.fvx * h;
            s.fvz += (stiffness * (frontZ - s.fz) - c * s.fvz) * h;
            s.fz += s.fvz * h;
        }
        if (dt == 0F && s.ax == 0F && s.az == 0F) {
            s.ax = targetX;
            s.az = targetZ;
            s.fx = frontX;
            s.fz = frontZ;
        }
        return new float[]{(float) Math.toRadians(Math.max(0F, s.ax)), (float) Math.toRadians(s.az),
                (float) Math.toRadians(Math.min(0F, s.fx)), (float) Math.toRadians(s.fz)};
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
