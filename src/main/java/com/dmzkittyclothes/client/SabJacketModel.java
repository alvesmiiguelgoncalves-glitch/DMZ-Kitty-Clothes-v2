package com.dmzkittyclothes.client;

import com.dmzkittyclothes.KittyConfig;
import com.dmzkittyclothes.DmzKittyClothes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * S.A.B. Jacket model, converted from the Bedrock geometry "overcoat" (128x128 texture).
 * Every box sits on one of the vanilla humanoid parts, so it follows the player's pose.
 * The "breasts" bone is left out: the original add-on hides it for the armor too (chest_layer_visible = 0).
 */
public class SabJacketModel extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(DmzKittyClothes.MOD_ID, "sab_jacket"), "main");

    private final ModelPart collar;
    private final ModelPart waist;

    public SabJacketModel(ModelPart root) {
        super(root);
        this.collar = this.head.getChild("collar");
        this.waist = this.body.getChild("waist");
    }

    private static void applyExtra(ModelPart part, float px, float py, float pz,
                                   net.minecraftforge.common.ForgeConfigSpec.DoubleValue[] c) {
        part.xScale = KittyConfig.get(c[0]);
        part.yScale = KittyConfig.get(c[1]);
        part.zScale = KittyConfig.get(c[2]);
        part.x = px + KittyConfig.get(c[3]);
        part.y = py + KittyConfig.get(c[4]);
        part.z = pz + KittyConfig.get(c[5]);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F, new CubeDeformation(0.2F)), PartPose.offset(0F, 0F, 0F));
        head.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(64, 0).addBox(-4.5F, -1.5F, -4.5F, 9F, 3F, 9F, new CubeDeformation(0.1F)), PartPose.offset(0F, 0.5F, 0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0.3F)).texOffs(24, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0.4F)), PartPose.offset(0F, 0F, 0F));
        body.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(68, 26).addBox(-4.5F, -1F, -3F, 9F, 2F, 6F, new CubeDeformation(0.1F)), PartPose.offset(0F, 10.5F, 0F));
        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 16).addBox(-3F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.33F)).texOffs(32, 48).addBox(-3F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.43F)), PartPose.offset(-5F, 2F, 0F));
        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 0).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.33F)).texOffs(16, 48).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.43F)), PartPose.offset(5F, 2F, 0F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(86, 12).addBox(-3F, -1F, -3F, 5F, 8F, 6F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12F, 0F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(64, 12).addBox(-2F, -1F, -3F, 5F, 8F, 6F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12F, 0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    /** Copies the pose of the player model that is being rendered (DMZ keeps it in sync with its animation). */
    public void followPose(HumanoidModel<?> base) {
        this.head.copyFrom(base.head);
        this.body.copyFrom(base.body);
        this.rightArm.copyFrom(base.rightArm);
        this.leftArm.copyFrom(base.leftArm);
        this.rightLeg.copyFrom(base.rightLeg);
        this.leftLeg.copyFrom(base.leftLeg);
        this.hat.copyFrom(base.hat);
        this.head.visible = true;
        this.body.visible = true;
        this.rightArm.visible = true;
        this.leftArm.visible = true;
        this.rightLeg.visible = true;
        this.leftLeg.visible = true;
        this.hat.visible = false;
        float ax = KittyConfig.get(KittyConfig.ARM_SCALE_X), ay = KittyConfig.get(KittyConfig.ARM_SCALE_Y), az = KittyConfig.get(KittyConfig.ARM_SCALE_Z);
        for (net.minecraft.client.model.geom.ModelPart arm : new net.minecraft.client.model.geom.ModelPart[]{this.rightArm, this.leftArm}) {
            arm.xScale = ax;
            arm.yScale = ay;
            arm.zScale = az;
        }
        float sx = KittyConfig.get(KittyConfig.SKIRT_SCALE_X), sy = KittyConfig.get(KittyConfig.SKIRT_SCALE_Y), sz = KittyConfig.get(KittyConfig.SKIRT_SCALE_Z);
        float ox = KittyConfig.get(KittyConfig.SKIRT_OFFSET_X), oy = KittyConfig.get(KittyConfig.SKIRT_OFFSET_Y), oz = KittyConfig.get(KittyConfig.SKIRT_OFFSET_Z);
        this.rightLeg.xScale = this.leftLeg.xScale = sx;
        this.rightLeg.yScale = this.leftLeg.yScale = sy;
        this.rightLeg.zScale = this.leftLeg.zScale = sz;
        this.rightLeg.x -= ox;
        this.leftLeg.x += ox;
        this.rightLeg.y += oy;
        this.leftLeg.y += oy;
        this.rightLeg.z += oz;
        this.leftLeg.z += oz;
        applyExtra(this.waist, 0F, 10.5F, 0F, KittyConfig.WAIST);
        applyExtra(this.collar, 0F, 0.5F, 0F, KittyConfig.COLLAR);
    }
}
