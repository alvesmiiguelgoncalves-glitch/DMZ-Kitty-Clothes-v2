package com.dmzkittyclothes;

import com.mojang.logging.LogUtils;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * S.A.B. Jacket for DragonMine Z. Model and textures by Novatsugs (Novatsugs Extra Clothes), used with the creator's permission.
 */
@Mod(DmzKittyClothes.MOD_ID)
public class DmzKittyClothes {

    public static final String MOD_ID = "dmz_kitty_clothes";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<Item> SAB_JACKET = ITEMS.register("sab_jacket",
            () -> new SabJacketItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DYE_KIT_2 = ITEMS.register("dye_kit_2", () -> new DyeKitItem(new Item.Properties()));
    public static final RegistryObject<Item> DYE_KIT_3 = ITEMS.register("dye_kit_3", () -> new DyeKitItem(new Item.Properties()));

    public static final java.util.List<RegistryObject<Item>> GLOVES = new java.util.ArrayList<>();
    static {
        for (String n : new String[]{"gloves", "fingerless_gloves", "long_gloves", "long_fingerless_gloves"}) {
            GLOVES.add(ITEMS.register(n, () -> new GlovesItem(new Item.Properties().stacksTo(1), n)));
        }
    }

    public static final java.util.List<RegistryObject<Item>> SUITS = new java.util.ArrayList<>();
    static {
        for (String n : new String[]{"jumpsuit", "jumpsuit_shorts", "jumpsuit_short_sleeves", "jumpsuit_short_sleeves_shorts",
                "jumpsuit_sleeveless", "jumpsuit_sleeveless_shorts", "jumpsuit_vest", "jumpsuit_vest_shorts"}) {
            SUITS.add(ITEMS.register(n, () -> new SuitItem(new Item.Properties().stacksTo(1), n)));
        }
    }

    public static final java.util.List<RegistryObject<Item>> VESTS = new java.util.ArrayList<>();
    public static final java.util.List<RegistryObject<Item>> SASHES = new java.util.ArrayList<>();
    static {
        VESTS.add(ITEMS.register("saiyan_vest_basic", () -> AccessoryItem.threeColors(new Item.Properties().stacksTo(1), "saiyan_vest_basic", true)));
        SASHES.add(ITEMS.register("saiyaman_belt", () -> AccessoryItem.colored(new Item.Properties().stacksTo(1), "saiyaman_belt", false)));
        for (String n : new String[]{"sash", "sash_tails", "sash_left_tail", "sash_right_tail"}) {
            SASHES.add(ITEMS.register(n, () -> new AccessoryItem(new Item.Properties().stacksTo(1), n, false, false)));
        }
    }

    /** Armadura Saiyajin Completa (slot colete, 3 cores) e saias (slot saia, 2 cores). */
    public static final java.util.List<RegistryObject<Item>> ARMORS = new java.util.ArrayList<>();
    public static final java.util.List<RegistryObject<Item>> SKIRTS = new java.util.ArrayList<>();
    static {
        ARMORS.add(ITEMS.register("saiyan_armor_full", () -> AccessoryItem.threeColors(new Item.Properties().stacksTo(1), "saiyan_armor_full", true)));
        for (String n : new String[]{"saiyan_skirt_mid", "saiyan_skirt_side"}) {
            SKIRTS.add(ITEMS.register(n, () -> new AccessoryItem(new Item.Properties().stacksTo(1), n, true, false)));
        }
    }

    /** Itens de braco (slot "gloves", exibido como "Bracos"), de perna (slot "legs") e cachecois (slot "scarf"). */
    public static final java.util.List<RegistryObject<Item>> ARM_ITEMS = new java.util.ArrayList<>();
    public static final java.util.List<RegistryObject<Item>> LEG_ITEMS = new java.util.ArrayList<>();
    public static final java.util.List<RegistryObject<Item>> SCARVES = new java.util.ArrayList<>();
    /** Capas (slot "cape", exibido como "Costas"). */
    public static final java.util.List<RegistryObject<Item>> CAPES = new java.util.ArrayList<>();
    static {
        CAPES.add(ITEMS.register("piccolo_cape", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "piccolo_cape", false, false)));
    }
    static {
        ARM_ITEMS.add(ITEMS.register("wristbands", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "wristbands", false, false)));
        ARM_ITEMS.add(ITEMS.register("arm_wrap", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "arm_wrap", false, false)));
        ARM_ITEMS.add(ITEMS.register("arm_guard", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "arm_guard", true, false)));
        LEG_ITEMS.add(ITEMS.register("leg_wrap", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "leg_wrap", false, false)));
        SCARVES.add(ITEMS.register("long_scarf", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "long_scarf", false, false)));
        SCARVES.add(ITEMS.register("short_scarf", () -> new AccessoryItem(new Item.Properties().stacksTo(1), "short_scarf", false, false)));
    }

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MOD_ID))
                    .icon(() -> SAB_JACKET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(SAB_JACKET.get());
                        output.accept(DYE_KIT_2.get());
                        output.accept(DYE_KIT_3.get());
                        GLOVES.forEach(g -> output.accept(g.get()));
                        SUITS.forEach(g -> output.accept(g.get()));
                        VESTS.forEach(g -> output.accept(g.get()));
                        ARMORS.forEach(g -> output.accept(g.get()));
                        SKIRTS.forEach(g -> output.accept(g.get()));
                        SASHES.forEach(g -> output.accept(g.get()));
                        ARM_ITEMS.forEach(g -> output.accept(g.get()));
                        LEG_ITEMS.forEach(g -> output.accept(g.get()));
                        SCARVES.forEach(g -> output.accept(g.get()));
                        CAPES.forEach(g -> output.accept(g.get()));
                    })
                    .build());

    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    public static final RegistryObject<net.minecraft.world.item.crafting.RecipeSerializer<?>> JACKET_DYE_RECIPE =
            RECIPES.register("sab_jacket_dye", () -> SabJacketDyeRecipe.SERIALIZER);

    public static final RegistryObject<net.minecraft.world.item.crafting.RecipeSerializer<?>> WEIGHT_CAPE_DYE_RECIPE =
            RECIPES.register("weight_cape_dye", () -> WeightCapeDye.SERIALIZER);

    public DmzKittyClothes() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        TABS.register(bus);
        RECIPES.register(bus);
        bus.addListener(this::commonSetup);
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, KittyConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Agua no caldeirao remove as tres cores.
        event.enqueueWork(() -> {
            CauldronInteraction.WATER.put(SAB_JACKET.get(), SabJacketItem::washInCauldron);
            CauldronInteraction.WATER.put(com.dragonminez.common.init.MainItems.WEIGHT_PICCOLO_CAPE.get(), WeightCapeDye::wash);
            GLOVES.forEach(g -> CauldronInteraction.WATER.put(g.get(), CauldronInteraction.DYED_ITEM));
            SUITS.forEach(g -> CauldronInteraction.WATER.put(g.get(), CauldronInteraction.DYED_ITEM));
            VESTS.forEach(g -> CauldronInteraction.WATER.put(g.get(), SabJacketItem::washInCauldron));
            ARMORS.forEach(g -> CauldronInteraction.WATER.put(g.get(), SabJacketItem::washInCauldron));
            SKIRTS.forEach(g -> CauldronInteraction.WATER.put(g.get(), SabJacketItem::washInCauldron));
            SASHES.forEach(g -> CauldronInteraction.WATER.put(g.get(), CauldronInteraction.DYED_ITEM));
            java.util.stream.Stream.of(ARM_ITEMS, LEG_ITEMS, SCARVES, CAPES).flatMap(java.util.List::stream)
                    .forEach(g -> CauldronInteraction.WATER.put(g.get(), g.getId().getPath().equals("arm_guard") ? SabJacketItem::washInCauldron : CauldronInteraction.DYED_ITEM));
        });
    }
}
