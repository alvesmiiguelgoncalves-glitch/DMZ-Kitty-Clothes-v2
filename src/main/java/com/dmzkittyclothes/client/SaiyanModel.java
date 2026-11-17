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
 * Geometria copiada da armadura do Vegeta do DMZ (geo/armor/armorcape.geo.json), em UV de skin 64x64:
 * armadura completa = corpo + bracos + ombreiras (6x4x5); saia = paineis laterais (1x8x4) e, opcionalmente,
 * painel frontal (5x7x1). Rotacoes e pivos convertidos de Bedrock (y para cima) para ModelPart (y para baixo).
 */
public class SaiyanModel extends SuitModel {

    public static final ModelLayerLocation ARMOR_LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "saiyan_armor"), "main");
    public static final ModelLayerLocation SKIRT_MID_LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "saiyan_skirt_mid"), "main");
    public static final ModelLayerLocation SKIRT_SIDE_LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "saiyan_skirt_side"), "main");

    public SaiyanModel(ModelPart root) {
        super(root);
    }

    private static PartDefinition base(MeshDefinition mesh) {
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));
        return root;
    }

    public static LayerDefinition createArmorLayer() {
        float t = KittyConfig.get(KittyConfig.ARMOR_THICKNESS);
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = base(mesh);
        CubeDeformation d = new CubeDeformation(t);
        CubeDeformation pad = new CubeDeformation(0.1F + t * 0.5F);
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, d), PartPose.ZERO);
        PartDefinition right = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3F, -2F, -2F, 4F, 12F, 4F, d), PartPose.offset(-5F, 2F, 0F));
        PartDefinition left = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1F, -2F, -2F, 4F, 12F, 4F, d), PartPose.offset(5F, 2F, 0F));
        right.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(42, 37).addBox(-2F, -2F, -2F, 6F, 4F, 5F, pad),
                PartPose.offsetAndRotation(-2.7F, -0.75F, -0.5F, 0F, 0F, (float) Math.toRadians(7.5)));
        left.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(25, 33).addBox(-1F, -2F, -2.5F, 6F, 4F, 5F, pad),
                PartPose.offsetAndRotation(-0.25F, -0.35F, 0F, 0F, 0F, (float) Math.toRadians(-7.5)));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createSkirtLayer(boolean middle) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = base(mesh);
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        body.addOrReplaceChild("panel_r", CubeListBuilder.create().texOffs(51, 50).addBox(-2F, -2F, -2F, 1F, 8F, 4F),
                PartPose.offsetAndRotation(-2.85F, 10.65F, 0F, 0F, 0F, (float) Math.toRadians(7.5)));
        body.addOrReplaceChild("panel_l", CubeListBuilder.create().texOffs(51, 50).addBox(-1F, -2F, -2F, 1F, 8F, 4F),
                PartPose.offsetAndRotation(4.8F, 10.25F, 0F, 0F, 0F, (float) Math.toRadians(-7.5)));
        if (middle) {
            body.addOrReplaceChild("panel_f", CubeListBuilder.create().texOffs(3, 56).addBox(-2F, -2F, -1F, 5F, 7F, 1F),
                    PartPose.offsetAndRotation(-0.5F, 10.9F, -1.65F, (float) Math.toRadians(-15), 0F, 0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }
}
