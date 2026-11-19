package com.dmzkittyclothes.client;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Fisica da capa: 3 segmentos em cadeia, cada um uma mola criticamente amortecida (sem tremer).
 * O vento (velocidade para frente, queda, lado, carregar ki) e o mesmo do cachecol; o segmento de baixo tem alvo maior e mola
 * mais mole, entao a capa abre em leque ao correr. Devolve a rotacao RELATIVA de cada segmento (o de baixo gira dentro do de cima).
 */
public final class CapePhysics {

    private static final class State {
        final float[] ax = new float[3], vx = new float[3], az = new float[3], vz = new float[3];
        float time = Float.NaN;
    }

    private static final Map<LivingEntity, State> STATES = new WeakHashMap<>();
    private static final float[] GAIN = {0.6F, 0.95F, 1.3F};
    private static final float[] SOFT = {1.0F, 0.8F, 0.65F};

    private CapePhysics() {
    }

    /** @return {x1,x2,x3,z1,z2,z3} em radianos (relativos ao segmento de cima). */
    public static float[] update(LivingEntity e, float partial, float swing, float stiffness, boolean charging) {
        State s = STATES.computeIfAbsent(e, k -> new State());
        float now = (e.tickCount + partial) / 20F;
        float dt = Float.isNaN(s.time) ? 0F : now - s.time;
        boolean first = Float.isNaN(s.time);
        if (dt < 0F || dt > 0.3F) {
            dt = 0F;
            java.util.Arrays.fill(s.vx, 0F);
            java.util.Arrays.fill(s.vz, 0F);
        }
        s.time = now;

        double vx = e.getX() - e.xo, vy = e.getY() - e.yo, vz = e.getZ() - e.zo;
        double yaw = Math.toRadians(e.yBodyRot);
        double forward = vx * -Math.sin(yaw) + vz * Math.cos(yaw);
        double side = vx * Math.cos(yaw) + vz * Math.sin(yaw);

        float wind = 2F + clamp((float) (forward * 90D), 0F, 40F) + clamp((float) (-vy * 35D), 0F, 30F) + (float) Math.sin(now * 1.1F) * 1.5F;
        float lateral = clamp((float) (side * 100D), -20F, 20F);
        if (charging) {
            wind = 35F + (float) Math.sin(now * 5.5F) * 12F;
            lateral = (float) Math.sin(now * 4.3F) * 12F;
        }
        wind *= swing;
        lateral *= swing;

        for (int i = 0; i < 3; i++) {
            float tx = wind * GAIN[i], tz = lateral * GAIN[i];
            float k = stiffness * SOFT[i], c = 2F * (float) Math.sqrt(k);
            if (first && dt == 0F) {
                s.ax[i] = tx;
                s.az[i] = tz;
            }
            int steps = Math.max(1, Math.round(dt / 0.01F));
            float h = dt / steps;
            for (int n = 0; n < steps && dt > 0F; n++) {
                s.vx[i] += (k * (tx - s.ax[i]) - c * s.vx[i]) * h;
                s.ax[i] += s.vx[i] * h;
                s.vz[i] += (k * (tz - s.az[i]) - c * s.vz[i]) * h;
                s.az[i] += s.vz[i] * h;
            }
        }
        float[] r = new float[6];
        for (int i = 0; i < 3; i++) {
            float absX = Math.max(0F, s.ax[i]), prevX = i == 0 ? 0F : Math.max(0F, s.ax[i - 1]);
            r[i] = (float) Math.toRadians(absX - prevX);
            r[3 + i] = (float) Math.toRadians(s.az[i] - (i == 0 ? 0F : s.az[i - 1]));
        }
        return r;
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
