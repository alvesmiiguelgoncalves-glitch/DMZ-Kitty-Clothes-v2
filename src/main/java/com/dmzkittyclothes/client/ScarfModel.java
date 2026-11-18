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
    private static final int[][] NO_FILLS = {};
    // Pontas com 1 px de espessura; as laterais/topo/base nao existem na textura, entao sao pintadas com a cor da tira (um pouco mais escura)
    // para a ponta nao sumir vista de lado. Ponta de tras: u=32, v=20 (w=3, h=11, d=1).
    private static final int[][] BACK_FILLS = {
            {32, 21, 33, 32, 38, 25, 82}, {33, 21, 36, 32, 38, 25, 90}, {36, 21, 37, 32, 38, 25, 82},
            {33, 20, 36, 21, 38, 25, 92}, {36, 20, 39, 21, 38, 25, 72},
            {37, 21, 40, 32, 38, 25, 90, 1}}; // tapa os buracos da tira (pixels transparentes dentro dela)
    // Ponta da frente: u=25, v=20 (w=2, h=10, d=1).
    private static final int[][] FRONT_FILLS = {
            {25, 21, 26, 31, 27, 25, 82}, {28, 21, 29, 31, 27, 25, 82}, {29, 21, 31, 31, 27, 25, 90},
            {26, 20, 28, 21, 27, 25, 92}, {28, 20, 30, 21, 27, 25, 72},
            {26, 21, 28, 31, 27, 25, 90, 1}};
    public static final TintTextures.Region TAIL_ONLY = new TintTextures.Region(true, new int[][]{BACK_RECT, {32, 20, 39, 32}}, BACK_FILLS);
    public static final TintTextures.Region FRONT_ONLY = new TintTextures.Region(true, new int[][]{FRONT_RECT, {25, 20, 31, 31}}, FRONT_FILLS);
    public static final TintTextures.Region BODY_ONLY = new TintTextures.Region(false, new int[][]{BACK_RECT, FRONT_RECT}, NO_FILLS);

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
        // face de tras em (37,21) com d=1: u = 37 - (d + w + d) = 32, v = 21 - d = 20. x de -4 a -1 (lado direito do jogador, visto de tras).
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(32, 20).addBox(-1.5F, 0F, 0F, 3F, 11F, 1F),
                PartPose.offset(-2.5F, 1F, 2F + t - 0.4F));
        // face da frente em (26,21) com d=1: u = 26 - d = 25, v = 20. x de 2 a 4 (lado esquerdo do jogador), preso ao pescoco.
        body.addOrReplaceChild("front_tail", CubeListBuilder.create().texOffs(25, 20).addBox(-1F, 0F, -1F, 2F, 10F, 1F),
                PartPose.offset(3F, 1F, -2F - t + 0.4F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
