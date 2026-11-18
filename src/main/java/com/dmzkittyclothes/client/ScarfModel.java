package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dmzkittyclothes.KittyConfig;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Cachecol longo: o corpo desenha o pescoco e a ponta da frente; a ponta de tras (pixels x37-39, y21-31) e a da frente
 * (x26-27, y21-30) sao pecas separadas ("tail" e "front_tail") presas ao pescoco que balancam.
 */
public class ScarfModel extends SuitModel {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "scarf_long"), "main");

    /** Pixels da ponta de tras na textura (apagados do corpo e mantidos so na peca que balanca). */
    private static final int[] BACK_RECT = {37, 21, 40, 32};
    /** Ponta da frente: pixels x26-27, y21-30 (face da frente, lado esquerdo do jogador). */
    private static final int[] FRONT_RECT = {26, 21, 28, 31};
    public static final TintTextures.Region TAIL_ONLY = new TintTextures.Region(true, BACK_RECT);
    public static final TintTextures.Region FRONT_ONLY = new TintTextures.Region(true, FRONT_RECT);
    public static final TintTextures.Region BODY_ONLY = new TintTextures.Region(false, BACK_RECT, FRONT_RECT);

    public final ModelPart tail;
    public final ModelPart frontTail;

    public ScarfModel(ModelPart root) {
        super(root);
        this.tail = this.body.getChild("tail");
        this.frontTail = this.body.getChild("front_tail");
    }

    public static LayerDefinition createLayer() {
        float t = KittyConfig.get(KittyConfig.SCARF_THICKNESS);
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(t)), PartPose.ZERO);
        // face de tras em (37,21): u = 37 - (d + w + d) = 32, v = 21 - d = 20. x de -4 a -1 (lado direito do jogador, visto de tras).
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(32, 20).addBox(-1.5F, 0F, 0F, 3F, 11F, 1F),
                PartPose.offset(-2.5F, 1F, 2F + t));
        // face da frente em (26,21): u = 26 - d = 25, v = 20. x de 2 a 4 (lado esquerdo do jogador), preso ao pescoco.
        body.addOrReplaceChild("front_tail", CubeListBuilder.create().texOffs(25, 20).addBox(-1F, 0F, -1F, 2F, 10F, 1F),
                PartPose.offset(3F, 1F, -2F - t));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
