package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dmzkittyclothes.SabJacketItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Draws the jacket through the Curios renderer. DMZ forwards third-party layers (including Curios') to its own
 * player renderer, with the vanilla player model kept in sync with the DMZ animation. That sync is made exact by
 * {@code VanillaModelSyncMixin} (see README), so the jacket parts can simply copy the base model's pose.
 */
public class SabJacketRenderer implements ICurioRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/sab_jacket.png");
    // Dyed: the grayscale pixels are tinted; the pixels that the original marks as "not dyeable" (alpha < 255 in
    // the Bedrock grayscale texture) are drawn again untinted from this second texture.
    private static final ResourceLocation TEXTURE_DYED = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/sab_jacket_dyed.png");
    private static final ResourceLocation TEXTURE_LOGO = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/sab_jacket_dyed_color2.png");
    private static final ResourceLocation TEXTURE_STRIPE = new ResourceLocation(DmzKittyClothes.MOD_ID, "textures/models/sab_jacket_dyed_color3.png");

    private final SabJacketModel model;

    public SabJacketRenderer(SabJacketModel model) {
        this.model = model;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> parent, MultiBufferSource buffer, int light,
                                                                          float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(stack.getItem() instanceof SabJacketItem item)) return;
        if (!(parent.getModel() instanceof HumanoidModel<?> base)) return;

        model.young = false;
        model.crouching = base.crouching;
        model.followPose(base);

        // 1) textura original completa (cores de fabrica + pixels fixos escuros)
        draw(buffer, TEXTURE, stack.hasFoil(), poseStack, light, 0xFFFFFF);
        // 2) cada regiao tingida e desenhada por cima (mesma geometria, mascara em tons de cinza * cor)
        if (item.hasCustomColor(stack)) draw(buffer, TEXTURE_DYED, false, poseStack, light, item.getColor(stack));
        if (SabJacketItem.hasLogoColor(stack)) draw(buffer, TEXTURE_LOGO, false, poseStack, light, SabJacketItem.getLogoColor(stack));
        if (SabJacketItem.hasStripeColor(stack)) draw(buffer, TEXTURE_STRIPE, false, poseStack, light, SabJacketItem.getStripeColor(stack));
    }

    private void draw(MultiBufferSource buffer, ResourceLocation tex, boolean foil, PoseStack poseStack, int light, int color) {
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(tex), false, foil);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }
}
