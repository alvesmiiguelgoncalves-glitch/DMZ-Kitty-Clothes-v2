package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dmzkittyclothes.KittyConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Versao "para tingir" de uma textura em tons de cinza: estica os cinzas para o intervalo [floor, 1],
 * assim o sombreado/detalhes continuam visiveis depois de multiplicar pela cor. Gerada na hora (nenhum arquivo extra).
 */
public final class TintTextures {

    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();
    private static float cachedFloor = -1F;
    private static float cachedDark = -1F;
    private static float cachedColorFloor = -1F;

    private TintTextures() {
    }

    /** Textura ja tingida com {@code color} (usar com cor de vertice branca). */
    public static ResourceLocation get(ResourceLocation src, int color) {
        return get(src, color, true);
    }

    /** enhance=false: multiplicacao simples pelo cinza original (usado na jaqueta). */
    public static ResourceLocation get(ResourceLocation src, int color, boolean enhance) {
        float floor = KittyConfig.get(KittyConfig.TINT_FLOOR);
        float dark = KittyConfig.get(KittyConfig.TINT_DARK_DETAIL);
        float cf = KittyConfig.get(KittyConfig.TINT_COLOR_FLOOR);
        if (floor != cachedFloor || dark != cachedDark || cf != cachedColorFloor || CACHE.size() > 128) {
            clear();
            cachedFloor = floor;
            cachedDark = dark;
            cachedColorFloor = cf;
        }
        int rgb = color & 0xFFFFFF;
        return CACHE.computeIfAbsent(src + "#" + rgb + (enhance ? "" : "p"), k -> build(src, rgb, floor, dark, enhance));
    }

    private static int clamp(float v) {
        return Math.max(0, Math.min(255, Math.round(v)));
    }

    public static void clear() {
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        CACHE.values().forEach(tm::release);
        CACHE.clear();
    }

    private static ResourceLocation build(ResourceLocation src, int rgb, float floor, float dark, boolean enhance) {
        float colorFloor = KittyConfig.get(KittyConfig.TINT_COLOR_FLOOR);
        float cr = (rgb >> 16 & 0xFF) / 255F, cg = (rgb >> 8 & 0xFF) / 255F, cb = (rgb & 0xFF) / 255F;
        float lum = 0.299F * cr + 0.587F * cg + 0.114F * cb;
        // O realce de detalhe vale so para preto (corante preto = #1D1D21): qualquer canal acima de 40 desliga.
        boolean black = enhance && Math.max((rgb >> 16) & 0xFF, Math.max((rgb >> 8) & 0xFF, rgb & 0xFF)) <= 40;
        float lift = black ? dark * (1F - lum) * 0.5F : 0F;
        try (InputStream in = Minecraft.getInstance().getResourceManager().open(src);
             NativeImage img = NativeImage.read(in)) {
            int w = img.getWidth(), h = img.getHeight();
            int min = 255, max = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p = img.getPixelRGBA(x, y);
                    if ((p >>> 24) == 0) continue;
                    int r = p & 0xFF;
                    min = Math.min(min, r);
                    max = Math.max(max, r);
                }
            }
            NativeImage out = new NativeImage(w, h, true);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p = img.getPixelRGBA(x, y);
                    int a = p >>> 24;
                    if (a == 0) continue;
                    float n = max > min ? (float) ((p & 0xFF) - min) / (max - min) : 1F;
                    // preto: cinza esticado (floor); outras cores: realce mais leve (colorFloor)
                    float m = !enhance ? (p & 0xFF) / 255F : black ? floor + (1F - floor) * n : colorFloor + (1F - colorFloor) * n;
                    // cores escuras: soma um brilho que acompanha o sombreado, senao preto * cinza = preto liso
                    int r = clamp((cr * m + lift * n) * 255F);
                    int g = clamp((cg * m + lift * n) * 255F);
                    int b = clamp((cb * m + lift * n) * 255F);
                    // ABGR
                    out.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
                }
            }
            ResourceLocation id = new ResourceLocation(DmzKittyClothes.MOD_ID, "tint/" + src.getPath().replace('/', '_').replace(".png", "") + "_" + Integer.toHexString(rgb | 0x1000000).substring(1) + (enhance ? "" : "p"));
            Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(out));
            return id;
        } catch (IOException e) {
            return src;
        }
    }
}
