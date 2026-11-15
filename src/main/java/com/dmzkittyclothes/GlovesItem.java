package com.dmzkittyclothes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Luvas (slot Curios "gloves"). Sem tinta: textura "name.png"; tingida: "name_dyed.png" (tons de cinza) * cor.
 * Nas luvas longas as duas texturas sao iguais (o cinza ja e a aparencia padrao).
 */
public class GlovesItem extends Item implements ICurioItem, DyeableLeatherItem {

    private final ResourceLocation defaultTexture;
    private final ResourceLocation dyedTexture;

    public GlovesItem(Properties properties, String name) {
        super(properties);
        this.defaultTexture = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + ".png");
        this.dyedTexture = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + "_dyed.png");
    }

    public ResourceLocation defaultTexture() {
        return defaultTexture;
    }

    public ResourceLocation dyedTexture() {
        return dyedTexture;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
