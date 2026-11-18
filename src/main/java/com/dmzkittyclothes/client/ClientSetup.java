package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dmzkittyclothes.SabJacketItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = DmzKittyClothes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SabJacketModel.LAYER, SabJacketModel::createBodyLayer);
        event.registerLayerDefinition(GlovesModel.LAYER, GlovesModel::createBodyLayer);
        event.registerLayerDefinition(SuitModel.LAYER, SuitModel::createBodyLayer);
        event.registerLayerDefinition(SuitModel.VEST_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.VEST_THICKNESS)));
        event.registerLayerDefinition(SuitModel.WRIST_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.WRIST_THICKNESS)));
        event.registerLayerDefinition(SuitModel.ARM_WRAP_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.ARM_WRAP_THICKNESS)));
        event.registerLayerDefinition(SuitModel.ARM_GUARD_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.ARM_GUARD_THICKNESS)));
        event.registerLayerDefinition(SuitModel.LEG_WRAP_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.LEG_WRAP_THICKNESS)));
        event.registerLayerDefinition(ScarfModel.LAYER, ScarfModel::createLayer);
        event.registerLayerDefinition(SuitModel.SCARF_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.SCARF_THICKNESS)));
        event.registerLayerDefinition(SaiyanModel.ARMOR_LAYER, SaiyanModel::createArmorLayer);
        event.registerLayerDefinition(SaiyanModel.SKIRT_MID_LAYER, () -> SaiyanModel.createSkirtLayer(true));
        event.registerLayerDefinition(SaiyanModel.SKIRT_SIDE_LAYER, () -> SaiyanModel.createSkirtLayer(false));
        event.registerLayerDefinition(SuitModel.WAIST_LAYER, () -> SuitModel.createBodyLayer(com.dmzkittyclothes.KittyConfig.get(com.dmzkittyclothes.KittyConfig.WAIST_THICKNESS)));
    }

    @SubscribeEvent
    public static void reloadListeners(net.minecraftforge.client.event.RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) rm -> TintTextures.clear());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(DmzKittyClothes.SAB_JACKET.get(),
                    () -> new SabJacketRenderer(new SabJacketModel(Minecraft.getInstance().getEntityModels().bakeLayer(SabJacketModel.LAYER))));
            DmzKittyClothes.GLOVES.forEach(g -> {
                CuriosRendererRegistry.register(g.get(),
                        () -> new GlovesRenderer(new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(GlovesModel.LAYER))));
                ItemProperties.register(g.get(), new ResourceLocation(DmzKittyClothes.MOD_ID, "dyed"),
                        (stack, level, entity, seed) -> stack.getItem() instanceof com.dmzkittyclothes.GlovesItem item && item.hasCustomColor(stack) ? 1.0F : 0.0F);
            });
            DmzKittyClothes.SUITS.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new SuitRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(SuitModel.LAYER)))));
            DmzKittyClothes.VESTS.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new AccessoryRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(SuitModel.VEST_LAYER)))));
            DmzKittyClothes.ARMORS.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new AccessoryRenderer(new SaiyanModel(Minecraft.getInstance().getEntityModels().bakeLayer(SaiyanModel.ARMOR_LAYER)))));
            DmzKittyClothes.SKIRTS.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new AccessoryRenderer(new SaiyanModel(Minecraft.getInstance().getEntityModels().bakeLayer(
                            g.getId().getPath().endsWith("_mid") ? SaiyanModel.SKIRT_MID_LAYER : SaiyanModel.SKIRT_SIDE_LAYER)))));
            DmzKittyClothes.SASHES.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new AccessoryRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(SuitModel.WAIST_LAYER)))));
            DmzKittyClothes.ARM_ITEMS.forEach(g -> CuriosRendererRegistry.register(g.get(), () -> new AccessoryRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(
                    switch (g.getId().getPath()) {
                        case "wristbands" -> SuitModel.WRIST_LAYER;
                        case "arm_wrap" -> SuitModel.ARM_WRAP_LAYER;
                        default -> SuitModel.ARM_GUARD_LAYER;
                    })))));
            DmzKittyClothes.LEG_ITEMS.forEach(g -> CuriosRendererRegistry.register(g.get(),
                    () -> new AccessoryRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(SuitModel.LEG_WRAP_LAYER)))));
            DmzKittyClothes.SCARVES.forEach(g -> {
                if (g.getId().getPath().equals("long_scarf")) {
                    CuriosRendererRegistry.register(g.get(),
                            () -> new ScarfRenderer(new ScarfModel(Minecraft.getInstance().getEntityModels().bakeLayer(ScarfModel.LAYER))));
                } else {
                    CuriosRendererRegistry.register(g.get(),
                            () -> new AccessoryRenderer(new SuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(SuitModel.SCARF_LAYER))));
                }
            });
            // 1 = dyed: the item model switches to the grayscale icon, which is tinted with the dye color.
            ItemProperties.register(DmzKittyClothes.SAB_JACKET.get(), new ResourceLocation(DmzKittyClothes.MOD_ID, "dyed"),
                    (stack, level, entity, seed) -> stack.getItem() instanceof SabJacketItem item && item.hasCustomColor(stack) ? 1.0F : 0.0F);
        });
    }

    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof DyeableLeatherItem dyeable && dyeable.hasCustomColor(stack)) {
                return dyeable.getColor(stack);
            }
            return 0xFFFFFF;
        }, DmzKittyClothes.SAB_JACKET.get());
        event.register((stack, tintIndex) -> tintIndex == 0 && stack.getItem() instanceof DyeableLeatherItem d && d.hasCustomColor(stack) ? d.getColor(stack) : 0xFFFFFF,
                java.util.stream.Stream.of(DmzKittyClothes.GLOVES, DmzKittyClothes.SUITS, DmzKittyClothes.VESTS, DmzKittyClothes.ARMORS, DmzKittyClothes.SKIRTS, DmzKittyClothes.SASHES, DmzKittyClothes.ARM_ITEMS, DmzKittyClothes.LEG_ITEMS, DmzKittyClothes.SCARVES).flatMap(java.util.List::stream).map(g -> (net.minecraft.world.level.ItemLike) g.get()).toArray(net.minecraft.world.level.ItemLike[]::new));
    }
}
