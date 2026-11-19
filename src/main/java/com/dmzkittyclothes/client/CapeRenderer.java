package com.dmzkittyclothes.client;

import com.dmzkittyclothes.AccessoryItem;
import com.dmzkittyclothes.KittyConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/** Capa do Piccolo (slot Costas): faixa, colar e capa com fisica; tudo ajustavel na config [cape]. */
public class CapeRenderer implements ICurioRenderer {

    private final CapeModel model;

    public CapeRenderer(CapeModel model) {
        this.model = model;
    }

    private static void apply(ModelPart part, float px, float py, float pz, ForgeConfigSpec.DoubleValue[] c, float sign) {
        part.xScale = KittyConfig.get(c[0]);
        part.yScale = KittyConfig.get(c[1]);
        part.zScale = KittyConfig.get(c[2]);
        part.x = px + sign * KittyConfig.get(c[3]);
        part.y = py + KittyConfig.get(c[4]);
        part.z = pz + KittyConfig.get(c[5]);
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
        // a gola esta presa na cabeca: followPose NAO copia a cabeca (so corpo, bracos e pernas), entao sem esta linha a gola fica parada na origem
        model.head.copyFrom(base.head);
        model.head.visible = true; // a gola esta presa na cabeca (followPose esconde a cabeca por padrao)

        model.collarBase.visible = KittyConfig.getBool(KittyConfig.CAPE_SHOW_COLLAR);
        model.collarTop.visible = model.collarBase.visible && KittyConfig.getBool(KittyConfig.CAPE_SHOW_COLLAR_TOP);
        model.padR.visible = model.padL.visible = KittyConfig.getBool(KittyConfig.CAPE_SHOW_PADS);
        apply(model.padR, -2.7F, -0.75F, -0.5F, KittyConfig.CAPE_PADS, -1F);
        apply(model.padL, -0.25F, -0.35F, 0F, KittyConfig.CAPE_PADS, 1F);
        model.band.visible = KittyConfig.getBool(KittyConfig.CAPE_SHOW_BAND);
        apply(model.collarBase, 0F, 1F, 0F, KittyConfig.CAPE_COLLAR_BASE, 1F);
        apply(model.collarTop, 0F, 0F, 0F, KittyConfig.CAPE_COLLAR_TOP, 1F);
        apply(model.band, 0F, 0F, 0F, KittyConfig.CAPE_BAND, 1F);
        float thickness = KittyConfig.get(KittyConfig.CAPE_DEPTH);
        // a espessura cresce para dentro do pivo; empurro o pivo para fora pelo mesmo tanto, assim a face de dentro fica sempre em 2.5
        apply(model.cape1, 0F, CapeModel.CAPE_Y, CapeModel.CAPE_INNER_Z + thickness, KittyConfig.CAPE_BODY, 1F);
        for (ModelPart panel : model.panels) panel.zScale = thickness;

        if (KittyConfig.getBool(KittyConfig.CAPE_PHYSICS)) {
            float[] r = CapePhysics.update(slotContext.entity(), partialTicks, KittyConfig.get(KittyConfig.CAPE_SWING),
                    KittyConfig.get(KittyConfig.CAPE_STIFFNESS), chargingKi(slotContext.entity()));
            model.cape1.xRot = r[0];
            model.cape2.xRot = r[1];
            model.cape3.xRot = r[2];
            model.cape1.zRot = r[3];
            model.cape2.zRot = r[4];
            model.cape3.zRot = r[5];
        } else {
            model.cape1.xRot = model.cape2.xRot = model.cape3.xRot = 0F;
            model.cape1.zRot = model.cape2.zRot = model.cape3.zRot = 0F;
        }

        boolean dyed = item.hasCustomColor(stack);
        // com tinta: realce normal; sem tinta: multiplicacao por branco = textura original (so para poder pintar as bordas)
        ResourceLocation tex = TintTextures.get(item.mainTexture(), dyed ? item.getColor(stack) : 0xFFFFFF, dyed, CapeModel.EDGES);
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(tex), false, stack.hasFoil());
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** DMZ: carregando ki. Qualquer falha = nao esta carregando. */
    private static boolean chargingKi(LivingEntity e) {
        if (!(e instanceof net.minecraft.world.entity.player.Player p)) return false;
        try {
            com.dragonminez.common.stats.StatsData d = com.dragonminez.common.stats.StatsProvider.get(com.dragonminez.common.stats.StatsCapability.INSTANCE, p).orElse(null);
            return d != null && d.getStatus().isChargingKi();
        } catch (Throwable t) {
            return false;
        }
    }
}
