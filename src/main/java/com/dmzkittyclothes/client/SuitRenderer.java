package com.dmzkittyclothes.client;

import com.dmzkittyclothes.SuitItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class SuitRenderer implements ICurioRenderer {

    private final SuitModel model;

    public SuitRenderer(SuitModel model) {
        this.model = model;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> parent, MultiBufferSource buffer, int light,
                                                                          float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(stack.getItem() instanceof SuitItem item)) return;
        if (!(parent.getModel() instanceof HumanoidModel<?> base)) return;

        model.young = false;
        model.crouching = base.crouching;
        model.followPose(base);

        boolean dyed = item.hasCustomColor(stack);
        int color = dyed ? item.getColor(stack) : 0xFFFFFF;
        float r = 1.0F, g = 1.0F, b = 1.0F;
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer,
                RenderType.armorCutoutNoCull(dyed ? TintTextures.get(item.texture(), color) : item.texture()), false, stack.hasFoil());
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }
}
