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
 * - o colar fica preso na CABECA (a cabeca nao atravessa a gola); a faixa (textura do torso) fica presa no CORPO, separada do colar;
 * - faixa (band): caixa 8x5x4 no pescoco (UV do corpo: topo em (20,16), laterais a partir de y20);
 * - colar: peca de baixo = laje 8x2x8 (rede em (32,0)) e peca de cima = 6x4x5 (rede em (42,37)).
 */
public class CapeModel extends SuitModel {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "piccolo_cape"), "main");

    public final ModelPart collarBase, collarTop, band, padR, padL, cape1, cape2, cape3;
    public final ModelPart[] panels;

    public CapeModel(ModelPart root) {
        super(root);
        this.collarBase = this.head.getChild("collar_base");
        this.collarTop = this.head.getChild("collar_top");
        this.band = this.body.getChild("band");
        this.padR = this.rightArm.getChild("pad");
        this.padL = this.leftArm.getChild("pad");
        this.cape1 = this.body.getChild("cape1");
        this.cape2 = this.cape1.getChild("cape2");
        this.cape3 = this.cape2.getChild("cape3");
        this.panels = new ModelPart[]{this.cape1.getChild("panel"), this.cape2.getChild("panel"), this.cape3.getChild("panel")};
    }

    /** Posicao de repouso (usada pelo renderer junto com os deslocamentos da config). */
    public static final float CAPE_Y = -0.25F;
    /** Distancia da face de dentro da capa ate o centro do corpo (fica fora do corpo, dos bracos e da roupa). */
    public static final float CAPE_INNER_Z = 2.5F;


    /**
     * A capa tem 1 px de espessura, mas a textura so desenha a face de fora. Aqui pintamos as outras faces de cada segmento
     * (u=8, v=31/37/43, w=12, d=1): a de dentro = copia espelhada da de fora; laterais, topo e base = cor da capa, mais escura.
     */
    public static final TintTextures.Region EDGES = new TintTextures.Region(false, new int[0][], capeFills());

    private static int[][] capeFills() {
        int[] v = {31, 37, 43};
        int[] h = {6, 6, 8};
        java.util.List<int[]> out = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int r0 = v[i] + 1, sy = r0 + 2;
            out.add(new int[]{22, r0, 34, r0 + h[i], 9, r0, 85, 2});             // face de dentro
            out.add(new int[]{8, r0, 9, r0 + h[i], 14, sy, 80, 1});              // lado oeste
            out.add(new int[]{21, r0, 22, r0 + h[i], 14, sy, 80, 1});            // lado leste
            out.add(new int[]{21, v[i], 33, v[i] + 1, 14, sy, 70, 1});           // base
            if (i == 0) out.add(new int[]{9, 31, 21, 32, 14, sy, 90, 1});        // topo (nos outros segmentos o topo ja usa pixels da textura)
        }
        return out.toArray(new int[0][]);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        // colar, peca de baixo: laje 8x2x8, UV da 2a camada da cabeca (topo (40,0), laterais a partir de y8), sobre os ombros
        head.addOrReplaceChild("collar_base", CubeListBuilder.create().texOffs(32, 0).addBox(-4F, -1F, -4F, 8F, 2F, 8F, new CubeDeformation(0.3F)), PartPose.offset(0F, 1F, 0F));
        // colar, peca de cima: 6x4x5, rede em (42,37); inflada para aparecer em volta do pescoco
        head.addOrReplaceChild("collar_top", CubeListBuilder.create().texOffs(42, 37).addBox(-3F, -2F, -2.5F, 6F, 4F, 5F, new CubeDeformation(0.8F)), PartPose.offset(0F, 0F, 0F));
        // faixa do peito/ombros: caixa 8x5x4, UV do corpo (topo (20,16), laterais a partir de y20)
        body.addOrReplaceChild("band", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 5F, 4F, new CubeDeformation(0.5F)), PartPose.ZERO);
        PartDefinition right = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        PartDefinition left = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        // ombreiras: 6x4x5, rede em (42,37); a esquerda usa a mesma textura espelhada
        right.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(42, 37).addBox(-2F, -2F, -2F, 6F, 4F, 5F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-2.7F, -0.75F, -0.5F, 0F, 0F, (float) Math.toRadians(7.5)));
        left.addOrReplaceChild("pad", CubeListBuilder.create().texOffs(42, 37).mirror().addBox(-1F, -2F, -2.5F, 6F, 4F, 5F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-0.25F, -0.35F, 0F, 0F, 0F, (float) Math.toRadians(-7.5)));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));

        // capa: cape1 pendurada no pescoco; cape2 e cape3 penduradas na ponta do segmento de cima
        PartDefinition c1 = body.addOrReplaceChild("cape1", CubeListBuilder.create(), PartPose.offset(0F, CAPE_Y, CAPE_INNER_Z + 1F));
        c1.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 31).addBox(-6F, 0F, 0F, 12F, 6F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        PartDefinition c2 = c1.addOrReplaceChild("cape2", CubeListBuilder.create(), PartPose.offset(0F, 6F, 0F));
        c2.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 37).addBox(-6F, 0F, 0F, 12F, 6F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        PartDefinition c3 = c2.addOrReplaceChild("cape3", CubeListBuilder.create(), PartPose.offset(0F, 6F, 0F));
        c3.addOrReplaceChild("panel", CubeListBuilder.create().texOffs(8, 43).addBox(-6F, 0F, 0F, 12F, 8F, 1F), PartPose.rotation(0F, (float) Math.PI, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
