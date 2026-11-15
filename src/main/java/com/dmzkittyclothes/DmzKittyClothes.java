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

    public static final RegistryObject<Item> LOGO_DYE_KIT = ITEMS.register("logo_dye_kit", () -> new DyeKitItem(new Item.Properties()));
    public static final RegistryObject<Item> STRIPE_DYE_KIT = ITEMS.register("stripe_dye_kit", () -> new DyeKitItem(new Item.Properties()));

    public static final java.util.List<RegistryObject<Item>> GLOVES = new java.util.ArrayList<>();
    static {
        for (String n : new String[]{"gloves", "fingerless_gloves", "long_gloves", "long_fingerless_gloves"}) {
            GLOVES.add(ITEMS.register(n, () -> new GlovesItem(new Item.Properties().stacksTo(1), n)));
        }
    }

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MOD_ID))
                    .icon(() -> SAB_JACKET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(SAB_JACKET.get());
                        output.accept(LOGO_DYE_KIT.get());
                        output.accept(STRIPE_DYE_KIT.get());
                        GLOVES.forEach(g -> output.accept(g.get()));
                    })
                    .build());

    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    public static final RegistryObject<net.minecraft.world.item.crafting.RecipeSerializer<?>> JACKET_DYE_RECIPE =
            RECIPES.register("sab_jacket_dye", () -> SabJacketDyeRecipe.SERIALIZER);

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
            GLOVES.forEach(g -> CauldronInteraction.WATER.put(g.get(), CauldronInteraction.DYED_ITEM));
        });
    }
}
