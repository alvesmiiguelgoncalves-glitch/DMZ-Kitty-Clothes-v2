package com.dmzkittyclothes;

import net.minecraftforge.common.ForgeConfigSpec;

/** Config de cliente: config/dmz_kitty_clothes-client.toml. Aplica na hora (recarrega ao salvar o arquivo). */
public final class KittyConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue SKIRT_SCALE_X, SKIRT_SCALE_Y, SKIRT_SCALE_Z;
    public static final ForgeConfigSpec.DoubleValue SKIRT_OFFSET_X, SKIRT_OFFSET_Y, SKIRT_OFFSET_Z;
    public static final ForgeConfigSpec.DoubleValue TINT_FLOOR;
    public static final ForgeConfigSpec.DoubleValue TINT_DARK_DETAIL;
    public static final ForgeConfigSpec.DoubleValue TINT_COLOR_FLOOR;
    public static final ForgeConfigSpec.DoubleValue GLOVE_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue VEST_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue WAIST_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue ARMOR_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue SUIT_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue WRIST_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue ARM_WRAP_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue ARM_GUARD_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue LEG_WRAP_THICKNESS;
    public static final ForgeConfigSpec.DoubleValue SCARF_THICKNESS;
    public static final ForgeConfigSpec.BooleanValue SCARF_PHYSICS;
    public static final ForgeConfigSpec.DoubleValue SCARF_SWING, SCARF_STIFFNESS, SCARF_DEPTH, SCARF_RAISE;
    public static final ForgeConfigSpec.DoubleValue ARM_SCALE_X, ARM_SCALE_Y, ARM_SCALE_Z;

    /** {scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ} */
    public static final ForgeConfigSpec.DoubleValue[] WAIST = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] COLLAR = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] CAPE_COLLAR_BASE = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] CAPE_COLLAR_TOP = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] CAPE_BAND = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.DoubleValue[] CAPE_BODY = new ForgeConfigSpec.DoubleValue[6];
    public static final ForgeConfigSpec.BooleanValue CAPE_SHOW_COLLAR, CAPE_SHOW_BAND, CAPE_PHYSICS;
    public static final ForgeConfigSpec.DoubleValue CAPE_SWING, CAPE_STIFFNESS, CAPE_DEPTH;

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

        b.comment("Cores. contrastFloor e darkDetail so afetam o PRETO; colorFloor afeta as outras cores. Preto: o cinza mais escuro vira este valor (0 = contraste maximo, 1 = sem realce).",
                "Mais baixo = detalhes mais visiveis, mas a roupa fica mais escura. Aplica ao recarregar recursos (F3+T) ou reiniciar.").push("tint");
        TINT_COLOR_FLOOR = b.comment("Realce de detalhe nas cores que NAO sao preto: o cinza mais escuro da textura vira este valor (1 = sem realce, vanilla; menor = mais detalhe). Padrao 0.75").defineInRange("colorFloor", 0.75, 0.0, 1.0);
        TINT_FLOOR = b.defineInRange("contrastFloor", 0.5, 0.0, 1.0);
        TINT_DARK_DETAIL = b.comment("Detalhe no PRETO (corante preto ou hex ate #282828): quanto de brilho o sombreado soma. So afeta o preto. 0 = desliga, mais alto = detalhe mais visivel, porem mais claro").defineInRange("darkDetail", 0.14, 0.0, 1.0);
        b.pop();
        b.comment("Luvas. A espessura so vale depois de reiniciar o jogo.").push("gloves");
        GLOVE_THICKNESS = b.comment("Espessura extra do braco da luva (pixels). A manga da jaqueta usa 0.33 (interna) e 0.43 (externa): menor que isso = a luva fica por baixo da jaqueta").defineInRange("thickness", 0.2, 0.0, 1.0);
        b.pop();
        b.comment("Macacoes. A espessura so vale depois de reiniciar o jogo.").push("suit");
        SUIT_THICKNESS = b.comment("Espessura extra do macacao (pixels). Luvas usam 0.2 e a jaqueta 0.33+: o macacao deve ficar por baixo das duas").defineInRange("thickness", 0.15, 0.0, 1.0);
        b.pop();
        b.comment("Colete e faixas. A espessura so vale depois de reiniciar o jogo. Devem ficar acima do macacao (0.15).").push("accessories");
        VEST_THICKNESS = b.comment("Espessura extra do colete (pixels)").defineInRange("vestThickness", 0.25, 0.0, 1.0);
        WAIST_THICKNESS = b.comment("Espessura extra das faixas/cinto (pixels). A jaqueta usa 0.4 no corpo: abaixo disso a faixa fica por baixo dela").defineInRange("waistThickness", 0.3, 0.0, 1.0);
        ARMOR_THICKNESS = b.comment("Espessura extra da Armadura Saiyajin Completa (corpo e bracos, pixels). Fica acima do macacao (0.15)").defineInRange("armorThickness", 0.3, 0.0, 1.0);
        WRIST_THICKNESS = b.comment("Espessura das munhequeiras (pixels). Acima da luva (0.2)").defineInRange("wristThickness", 0.25, 0.0, 1.0);
        ARM_WRAP_THICKNESS = b.comment("Espessura da faixa de pano dos bracos (pixels)").defineInRange("armWrapThickness", 0.28, 0.0, 1.0);
        ARM_GUARD_THICKNESS = b.comment("Espessura do protetor de braco (pixels). A manga da jaqueta usa 0.33+: para ficar por cima dela, use mais de 0.45").defineInRange("armGuardThickness", 0.32, 0.0, 1.0);
        LEG_WRAP_THICKNESS = b.comment("Espessura da faixa de pano das pernas (pixels). Acima do macacao (0.15) e das botas (0.18)").defineInRange("legWrapThickness", 0.3, 0.0, 1.0);
        SCARF_THICKNESS = b.comment("Espessura dos cachecois (pixels). Acima de tudo, menos da jaqueta (corpo 0.4): armadura 0.3, colete 0.25").defineInRange("scarfThickness", 0.35, 0.0, 1.0);
        b.pop();
        b.comment("Fisica da ponta do cachecol longo (vale na hora, sem reiniciar).").push("scarf");
        SCARF_PHYSICS = b.comment("Liga/desliga o balanco da ponta do cachecol").define("physics", true);
        SCARF_SWING = b.comment("Quanto a ponta balanca (1.0 = padrao, 0.5 = metade, 0 = parada)").defineInRange("swing", 1.0, 0.0, 3.0);
        SCARF_STIFFNESS = b.comment("Rigidez da mola: maior = volta ao lugar mais rapido; menor = mais molenga/lenta").defineInRange("stiffness", 30.0, 2.0, 200.0);
        SCARF_DEPTH = b.comment("Espessura das pontas do cachecol longo em pixels (1.0 = 1 px; 0.5 = metade)").defineInRange("depth", 0.5, 0.1, 3.0);
        SCARF_RAISE = b.comment("Sobe as pontas do cachecol longo (pixels). Aumente se aparecer roupa entre o pescoco e a ponta").defineInRange("raise", 0.7, -3.0, 4.0);
        b.pop();
        b.comment("Capa do Piccolo (slot Costas). Cada parte tem escala e deslocamento, como a jaqueta.").push("cape");
        CAPE_SHOW_COLLAR = b.comment("Mostrar o colar (as duas pecas: base achatada + parte de cima)").define("showCollar", true);
        CAPE_SHOW_BAND = b.comment("Mostrar a faixa do peito/ombros (caixa 8x5x4 do pescoco)").define("showBand", true);
        CAPE_PHYSICS = b.comment("Liga/desliga a fisica da capa").define("physics", true);
        CAPE_SWING = b.comment("Quanto a capa balanca (1.0 = padrao, 0 = parada)").defineInRange("swing", 1.0, 0.0, 3.0);
        CAPE_STIFFNESS = b.comment("Rigidez da mola: maior = volta mais rapido; menor = mais lenta/molenga").defineInRange("stiffness", 22.0, 2.0, 200.0);
        CAPE_DEPTH = b.comment("Espessura do tecido da capa (pixels). 1.0 = 1 pixel").defineInRange("thickness", 1.0, 0.05, 3.0);
        extra(b, "collarBase", "Colar: peca de baixo (laje achatada 8x2x8).", CAPE_COLLAR_BASE, 1, 1, 1, 0, 0, 0);
        extra(b, "collarTop", "Colar: peca de cima (6x4x5).", CAPE_COLLAR_TOP, 1, 1, 1, 0, 0, 0);
        extra(b, "band", "Faixa do peito/ombros (caixa 8x5x4 do pescoco).", CAPE_BAND, 1, 1, 1, 0, 0, 0);
        extra(b, "body", "Capa (os 3 segmentos juntos): Largura/Altura(comprimento)/Profundidade e posicao.", CAPE_BODY, 1, 1, 1, 0, 0, 0);
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
