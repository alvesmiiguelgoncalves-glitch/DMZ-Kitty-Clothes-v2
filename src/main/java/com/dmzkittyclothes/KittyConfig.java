package com.dmzkittyclothes;

import net.minecraftforge.common.ForgeConfigSpec;

/** Config de cliente: config/dmz_kitty_clothes-client.toml. Aplica na hora (recarrega ao salvar o arquivo). */
public final class KittyConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue SKIRT_SCALE_X, SKIRT_SCALE_Y, SKIRT_SCALE_Z;
    public static final ForgeConfigSpec.DoubleValue SKIRT_OFFSET_X, SKIRT_OFFSET_Y, SKIRT_OFFSET_Z;
    public static final ForgeConfigSpec.DoubleValue TINT_FLOOR;
    public static final ForgeConfigSpec.DoubleValue TINT_DARK_DETAIL;
    public static final ForgeConfigSpec.DoubleValue GLOVE_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue SUIT_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue ARM_SCALE_X, ARM_SCALE_Y, ARM_SCALE_Z;

    /** {scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ} */
    public static final ForgeConfigSpec.DoubleValue[] WAIST = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] COLLAR = new ForgeConfigSpec.DoubleValue[6];

    private static void extra(ForgeConfigSpec.Builder b, String name, String comment, ForgeConfigSpec.DoubleValue[] out, double... d) {
        b.comment(comment, "Escala 1.0 = tamanho original; a escala parte do centro da peca. Deslocamentos em pixels.").push(name);
        out[0] = b.comment("Largura").defineInRange("scaleX", d[0], 0.1, 3.0);
        out[1] = b.comment("Altura").defineInRange("scaleY", d[1], 0.1, 3.0);
        out[2] = b.comment("Profundidade").defineInRange("scaleZ", d[2], 0.1, 3.0);
        out[3] = b.comment("Lado a lado").defineInRange("offsetX", d[3], -8.0, 8.0);
        out[4] = b.comment("Sobe (-) / desce (+)").defineInRange("offsetY", d[4], -8.0, 8.0);
        out[5] = b.comment("Frente (-) / tras (+)").defineInRange("offsetZ", d[5], -8.0, 8.0);
        b.pop();
    }

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("Saia da jaqueta (as caixas das pernas). Escala 1.0 = tamanho original.",
                "A escala parte do quadril: Y menor = saia mais curta (sobe a ponta de baixo).").push("skirt");
        SKIRT_SCALE_X = b.comment("Largura").defineInRange("scaleX", 1.0, 0.1, 3.0);
        SKIRT_SCALE_Y = b.comment("Altura").defineInRange("scaleY", 0.9, 0.1, 3.0);
        SKIRT_SCALE_Z = b.comment("Profundidade (frente/tras). Menor = mais espaco pra cauda").defineInRange("scaleZ", 0.87, 0.1, 3.0);
        SKIRT_OFFSET_X = b.comment("Abre (+) ou fecha (-) as duas metades, em pixels").defineInRange("offsetX", -0.6, -8.0, 8.0);
        SKIRT_OFFSET_Y = b.comment("Sobe (-) ou desce (+), em pixels").defineInRange("offsetY", 0.6, -8.0, 8.0);
        SKIRT_OFFSET_Z = b.comment("Move pra frente (-) ou pra tras (+), em pixels").defineInRange("offsetZ", 0.0, -8.0, 8.0);
        b.pop();

        b.comment("Mangas da jaqueta. Escala 1.0 = tamanho original.").push("arms");
        ARM_SCALE_X = b.defineInRange("scaleX", 1.0, 0.1, 3.0);
        ARM_SCALE_Y = b.comment("Comprimento").defineInRange("scaleY", 1.0, 0.1, 3.0);
        ARM_SCALE_Z = b.defineInRange("scaleZ", 1.0, 0.1, 3.0);
        b.pop();

        b.comment("Cores. Texturas tingidas: o cinza mais escuro vira este valor (0 = contraste maximo, 1 = sem realce).",
                "Mais baixo = detalhes mais visiveis, mas a roupa fica mais escura. Aplica ao recarregar recursos (F3+T) ou reiniciar.").push("tint");
        TINT_FLOOR = b.defineInRange("contrastFloor", 0.5, 0.0, 1.0);
        TINT_DARK_DETAIL = b.comment("Detalhe em cores escuras (preto, azul-marinho...): quanto de brilho o sombreado soma. 0 = desliga, mais alto = detalhe mais visivel, porem mais claro").defineInRange("darkDetail", 0.3, 0.0, 1.0);
        b.pop();
        b.comment("Luvas. A espessura so vale depois de reiniciar o jogo.").push("gloves");
        GLOVE_THICKNESS = b.comment("Espessura extra do braco da luva (pixels). A manga da jaqueta usa 0.33 (interna) e 0.43 (externa): menor que isso = a luva fica por baixo da jaqueta").defineInRange("thickness", 0.2, 0.0, 1.0);
        b.pop();
        b.comment("Macacoes. A espessura so vale depois de reiniciar o jogo.").push("suit");
        SUIT_THICKNESS = b.comment("Espessura extra do macacao (pixels). Luvas usam 0.2 e a jaqueta 0.33+: o macacao deve ficar por baixo das duas").defineInRange("thickness", 0.15, 0.0, 1.0);
        b.pop();
        extra(b, "waist", "Faixa/quadrado da cintura da jaqueta.", WAIST, 0.98, 1.15, 0.85, 0, 0, 0);
        extra(b, "collar", "Gola (parte em volta do pescoco/cabeca).", COLLAR, 0.98, 1.0, 0.98, 0, -0.1, 0);

        SPEC = b.build();
    }

    private KittyConfig() {
    }

    private static final java.nio.file.Path FILE =
            net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve(DmzKittyClothes.MOD_ID + "-client.toml");
    private static com.electronwill.nightconfig.core.Config disk;
    private static long lastCheck;
    private static long lastModified = -1;

    /** Le direto do arquivo (recheca a cada ~0,5 s), sem depender do recarregamento automatico do Forge. */
    public static float get(ForgeConfigSpec.DoubleValue v) {
        refresh();
        if (disk != null) {
            Object o = disk.get(v.getPath());
            if (o instanceof Number n) {
                return (float) n.doubleValue();
            }
        }
        try {
            return v.get().floatValue();
        } catch (IllegalStateException e) {
            return v.getDefault().floatValue();
        }
    }

    public static boolean getBool(ForgeConfigSpec.BooleanValue v) {
        refresh();
        if (disk != null && disk.get(v.getPath()) instanceof Boolean bo) return bo;
        try {
            return v.get();
        } catch (IllegalStateException e) {
            return v.getDefault();
        }
    }

    private static void refresh() {
        long now = System.currentTimeMillis();
        if (now - lastCheck < 500) return;
        lastCheck = now;
        try {
            long m = java.nio.file.Files.getLastModifiedTime(FILE).toMillis();
            if (m == lastModified) return;
            try (com.electronwill.nightconfig.core.file.CommentedFileConfig c =
                         com.electronwill.nightconfig.core.file.CommentedFileConfig.builder(FILE).build()) {
                c.load();
                disk = com.electronwill.nightconfig.core.Config.copy(c);
            }
            lastModified = m;
        } catch (Exception ignored) {
            // arquivo ainda nao existe ou esta sendo gravado: tenta de novo depois
        }
    }
}
