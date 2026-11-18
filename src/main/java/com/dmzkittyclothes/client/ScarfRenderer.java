package com.dmzkittyclothes.client;

import com.dmzkittyclothes.AccessoryItem;
import com.dmzkittyclothes.KittyConfig;
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

/** Cachecol longo: corpo (sem a ponta de tras) + ponta de tras com fisica de mola. */
public class ScarfRenderer implements ICurioRenderer {

    private final ScarfModel model;

    public ScarfRenderer(ScarfModel model) {
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

        if (KittyConfig.getBool(KittyConfig.SCARF_PHYSICS)) {
            float[] a = ScarfPhysics.update(slotContext.entity(), partialTicks,
                    KittyConfig.get(KittyConfig.SCARF_SWING), KittyConfig.get(KittyConfig.SCARF_STIFFNESS));
            model.tail.xRot = a[0];
            model.tail.zRot = a[1];
            model.frontTail.xRot = a[2];
            model.frontTail.zRot = a[3];
        } else {
            model.tail.xRot = (float) Math.toRadians(6);
            model.tail.zRot = 0F;
            model.frontTail.xRot = (float) Math.toRadians(-2);
            model.frontTail.zRot = 0F;
        }

        boolean dyed = item.hasCustomColor(stack);
        // sem tinta: multiplicacao simples por branco = textura original
        int color = dyed ? item.getColor(stack) : 0xFFFFFF;
        ResourceLocation bodyTex = TintTextures.get(item.mainTexture(), color, dyed, ScarfModel.BODY_ONLY);
        ResourceLocation tailTex = TintTextures.get(item.mainTexture(), color, dyed, ScarfModel.TAIL_ONLY);
        boolean foil = stack.hasFoil();

        ResourceLocation frontTex = TintTextures.get(item.mainTexture(), color, dyed, ScarfModel.FRONT_ONLY);

        // 1) corpo; 2) ponta de tras; 3) ponta da frente (cada uma com a sua parte da textura)
        model.tail.visible = false;
        model.frontTail.visible = false;
        draw(buffer, bodyTex, foil, poseStack, light);
        model.body.skipDraw = true;
        model.tail.visible = true;
        draw(buffer, tailTex, foil, poseStack, light);
        model.tail.visible = false;
        model.frontTail.visible = true;
        draw(buffer, frontTex, foil, poseStack, light);
        model.tail.visible = true;
        model.body.skipDraw = false;
    }

    private void draw(MultiBufferSource buffer, ResourceLocation tex, boolean foil, PoseStack poseStack, int light) {
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(tex), false, foil);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
