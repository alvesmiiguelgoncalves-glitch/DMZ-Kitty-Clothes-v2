package com.dmzkittyclothes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Colete / faixas / cinto (slots Curios "vest" e "waist"). Texturas em tons de cinza no UV de skin 64x64:
 * "name.png" = cor principal; "name_color2.png" (opcional) = segunda cor (Kit de Coloracao 2).
 * Regiao sem tinta usa o proprio cinza da textura.
 */
public class AccessoryItem extends Item implements ICurioItem, DyeableLeatherItem, MultiColor {

    private final ResourceLocation mainTexture;
    private final ResourceLocation secondTexture;
    private final boolean hidesShirt;
    private ResourceLocation fullDefault;
    private ResourceLocation default1;
    private ResourceLocation default2;

    /**
     * Item cuja aparencia padrao e colorida (ex.: cinto do Grande Saiyaman). Texturas: "name.png" (padrao completo),
     * "name_main.png" / "name_color2.png" (mascaras cinza da cor 1 e 2) e "name_default1/2.png" (o padrao so naquela regiao).
     * Texturas com o dobro do tamanho (128x128) funcionam normalmente: o modelo usa UV 64x64.
     */
    public static AccessoryItem colored(Properties properties, String name, boolean hidesShirt) {
        AccessoryItem item = new AccessoryItem(properties, name + "_main", true, hidesShirt, name + "_color2");
        item.fullDefault = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + ".png");
        item.default1 = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + "_default1.png");
        item.default2 = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + "_default2.png");
        return item;
    }

    public ResourceLocation fullDefault() {
        return fullDefault;
    }

    public ResourceLocation default1() {
        return default1;
    }

    public ResourceLocation default2() {
        return default2;
    }

    public AccessoryItem(Properties properties, String name, boolean hasSecond, boolean hidesShirt) {
        this(properties, name, hasSecond, hidesShirt, name + "_color2");
    }

    private AccessoryItem(Properties properties, String name, boolean hasSecond, boolean hidesShirt, String secondName) {
        super(properties);
        this.mainTexture = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + ".png");
        this.secondTexture = hasSecond ? new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + secondName + ".png") : null;
        this.hidesShirt = hidesShirt;
    }

    public ResourceLocation mainTexture() {
        return mainTexture;
    }

    /** null se o item so tem a cor principal. */
    public ResourceLocation secondTexture() {
        return secondTexture;
    }

    public boolean hidesShirt() {
        return hidesShirt;
    }

    @Override
    public boolean hasThird() {
        return false;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
