package com.dmzkittyclothes.client;

import com.dmzkittyclothes.AccessoryItem;
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

/** Colete e faixas: cor principal e (opcional) segunda cor, em camadas que nunca se sobrepoem. */
public class AccessoryRenderer implements ICurioRenderer {

    private final SuitModel model;

    public AccessoryRenderer(SuitModel model) {
        this.model = model;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> parent, MultiBufferSource buffer, int light,
                                                                          float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(stack.getItem() instanceof AccessoryItem item)) return;
        if (!(parent.getModel() instanceof HumanoidModel<?> base)) return;

        model.young = false;
        model.crouching = base.crouching;
        model.followPose(base);

        boolean foil = stack.hasFoil();
        boolean any = item.hasCustomColor(stack) || SabJacketItem.hasLogoColor(stack);
        if (item.fullDefault() != null) {
            // aparencia padrao colorida: sem tinta usa a textura completa; com tinta, cada regiao e tingida OU padrao (sem sobreposicao)
            if (!any) {
                draw(buffer, item.fullDefault(), foil, poseStack, light);
                return;
            }
            draw(buffer, item.hasCustomColor(stack) ? TintTextures.get(item.mainTexture(), item.getColor(stack)) : item.default1(), foil, poseStack, light);
            draw(buffer, SabJacketItem.hasLogoColor(stack) ? TintTextures.get(item.secondTexture(), SabJacketItem.getLogoColor(stack)) : item.default2(), false, poseStack, light);
            return;
        }
        ResourceLocation main = item.hasCustomColor(stack) ? TintTextures.get(item.mainTexture(), item.getColor(stack)) : item.mainTexture();
        draw(buffer, main, foil, poseStack, light);
        if (item.secondTexture() != null) {
            ResourceLocation second = SabJacketItem.hasLogoColor(stack)
                    ? TintTextures.get(item.secondTexture(), SabJacketItem.getLogoColor(stack)) : item.secondTexture();
            draw(buffer, second, false, poseStack, light);
        }
        if (item.thirdTexture() != null) {
            ResourceLocation third = SabJacketItem.hasStripeColor(stack)
                    ? TintTextures.get(item.thirdTexture(), SabJacketItem.getStripeColor(stack)) : item.thirdTexture();
            draw(buffer, third, false, poseStack, light);
        }
    }

    private void draw(MultiBufferSource buffer, ResourceLocation tex, boolean foil, PoseStack poseStack, int light) {
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(tex), false, foil);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
