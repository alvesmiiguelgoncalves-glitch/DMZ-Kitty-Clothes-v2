package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
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
 * Capa do Piccolo (textura 64x64 do usuario):
 * - capa: 3 segmentos de 12 de largura (6+6+8 de altura), face de tras em x9-20, y32-51; cada segmento pendura no de cima (fisica em cadeia);
 * - colar: caixa 8x5x4 no pescoco (UV do corpo: topo em (20,16), laterais a partir de y20);
 * - turbante: laje 8x2x8 na cabeca (UV da 2a camada da cabeca: topo (40,0), laterais a partir de y8);
 * - ombreiras: 6x4x5, rede em (44,37) (a esquerda usa a mesma textura espelhada).
 */
public class CapeModel extends SuitModel {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "piccolo_cape"), "main");

    public final ModelPart turban, collar, padR, padL, cape1, cape2, cape3;
    public final ModelPart[] panels;

    public CapeModel(ModelPart root) {
        super(root);
        this.turban = this.head.getChild("turban");
        this.collar = this.body.getChild("collar");
        this.padR = this.rightArm.getChild("pad");
        this.padL = this.leftArm.getChild("pad");
        this.cape1 = this.body.getChild("cape1");
        this.cape2 = this.cape1.getChild("cape2");
        this.cape3 = this.cape2.getChild("cape3");
        this.panels = new ModelPart[]{this.cape1.getChild("panel"), this.cape2.getChild("panel"), this.cape3.getChild("panel")};
    }

    /** Posicao de repouso (usada pelo renderer junto com os deslocamentos da config). */
    public static final float CAPE_Y = -0.25F, CAPE_Z = 2.6F;


    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("turban", CubeListBuilder.create().texOffs(32, 0).addBox(-4F, -8F, -4F, 8F, 2F, 8F, new CubeDeformation(0.6F)), PartPose.ZERO);
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 5F, 4F, new CubeDeformation(0.5F)), PartPose.ZERO);
        PartDefinition right = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        PartDefinition left = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        right.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(44, 37).addBox(-2F, -2F, -2F, 6F, 4F, 5F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-2.7F, -0.75F, -0.5F, 0F, 0F, (float) Math.toRadians(7.5)));
        left.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(44, 37).mirror().addBox(-1F, -2F, -2.5F, 6F, 4F, 5F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-0.25F, -0.35F, 0F, 0F, 0F, (float) Math.toRadians(-7.5)));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));

        // capa: cape1 pendurada no pescoco; cape2 e cape3 penduradas na ponta do segmento de cima
        PartDefinition c1 = body.addOrReplaceChild("cape1", CubeListBuilder.create(), PartPose.offset(0F, CAPE_Y, CAPE_Z));
        c1.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 31).addBox(-6F, 0F, 0F, 12F, 6F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        PartDefinition c2 = c1.addOrReplaceChild("cape2", CubeListBuilder.create(), PartPose.offset(0F, 6F, 0F));
        c2.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 37).addBox(-6F, 0F, 0F, 12F, 6F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        PartDefinition c3 = c2.addOrReplaceChild("cape3", CubeListBuilder.create(), PartPose.offset(0F, 6F, 0F));
        c3.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 43).addBox(-6F, 0F, 0F, 12F, 8F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
