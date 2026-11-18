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
    // Pontas com 2 px de espessura (relevo): as laterais/topo/base nao existem na textura, entao sao pintadas com a cor da tira (um pouco mais escura).
    // Ponta de tras: u=30, v=19 (w=3, h=11, d=2): oeste (30,21)-(32,32), frente (32,21)-(35,32), leste (35,21)-(37,32), topo (32,19)-(35,21), base (35,19)-(38,21)
    private static final int[][] BACK_FILLS = {
            {30, 21, 32, 32, 38, 25, 80}, {32, 21, 35, 32, 38, 25, 90}, {35, 21, 37, 32, 38, 25, 80},
            {32, 19, 35, 21, 38, 25, 90}, {35, 19, 38, 21, 38, 25, 70}};
    // Ponta da frente: u=24, v=19 (w=2, h=10, d=2): oeste (24,21)-(26,31), leste (28,21)-(30,31), costas (30,21)-(32,31), topo (26,19)-(28,21), base (28,19)-(30,21)
    private static final int[][] FRONT_FILLS = {
            {24, 21, 26, 31, 27, 25, 80}, {28, 21, 30, 31, 27, 25, 80}, {30, 21, 32, 31, 27, 25, 90},
            {26, 19, 28, 21, 27, 25, 90}, {28, 19, 30, 21, 27, 25, 70}};
    public static final TintTextures.Region TAIL_ONLY = new TintTextures.Region(true, new int[][]{BACK_RECT, {30, 19, 38, 32}}, BACK_FILLS);
    public static final TintTextures.Region FRONT_ONLY = new TintTextures.Region(true, new int[][]{FRONT_RECT, {24, 19, 32, 31}}, FRONT_FILLS);
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
        // face de tras em (37,21) com d=2: u = 37 - (d + w + d) = 30, v = 21 - d = 19. x de -4 a -1 (lado direito do jogador, visto de tras).
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 19).addBox(-1.5F, 0F, 0F, 3F, 11F, 2F),
                PartPose.offset(-2.5F, 1F, 2F + t - 0.3F));
        // face da frente em (26,21) com d=2: u = 26 - d = 24, v = 19. x de 2 a 4 (lado esquerdo do jogador), preso ao pescoco.
        body.addOrReplaceChild("front_tail", CubeListBuilder.create().texOffs(24, 19).addBox(-1F, 0F, -2F, 2F, 10F, 2F),
                PartPose.offset(3F, 1F, -2F - t + 0.3F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
