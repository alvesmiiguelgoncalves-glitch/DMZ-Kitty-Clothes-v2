package com.dmzkittyclothes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Macacao (slot Curios "bodysuit"). Textura em tons de cinza (UV de skin 64x64) * cor do corante;
 * sem tinta fica na cor cinza/branca original. Equipa com botao direito.
 */
public class SuitItem extends Item implements ICurioItem, DyeableLeatherItem {

    private final ResourceLocation texture;

    public SuitItem(Properties properties, String name) {
        super(properties);
        this.texture = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/" + name + ".png");
    }

    public ResourceLocation texture() {
        return texture;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
