package com.dmzkittyclothes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

import java.util.ArrayList;
import java.util.List;

/**
 * Jaqueta + corante(s) + 1 item marcador:
 *  - marcador da tag dmz_kitty_clothes:logo_marker (item "Kit de Coloracao 2": la branca + corante branco/farinha de osso) -> tinge a logo;
 *  - marcador da tag dmz_kitty_clothes:stripe_marker (item "Kit de Coloracao 3": la preta + corante preto/tinta de lula) -> tinge a listra.
 * Sem marcador, vale a receita normal de armadura de couro (cor principal), que ja existe no vanilla.
 * As tags podem ser trocadas por datapack.
 */
public class SabJacketDyeRecipe extends CustomRecipe {

    public static final TagKey<Item> LOGO_MARKER = TagKey.create(Registries.ITEM, new ResourceLocation(DmzKittyClothes.MOD_ID, "logo_marker"));
    public static final TagKey<Item> STRIPE_MARKER = TagKey.create(Registries.ITEM, new ResourceLocation(DmzKittyClothes.MOD_ID, "stripe_marker"));

    public static final RecipeSerializer<SabJacketDyeRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(SabJacketDyeRecipe::new);

    public SabJacketDyeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private record Parsed(ItemStack jacket, List<DyeItem> dyes, boolean logo, boolean stripe) {
    }

    private static Parsed parse(CraftingContainer c) {
        ItemStack jacket = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();
        boolean logo = false, stripe = false;
        for (int i = 0; i < c.getContainerSize(); i++) {
            ItemStack s = c.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() instanceof SabJacketItem) {
                if (!jacket.isEmpty()) return null;
                jacket = s;
            } else if (s.getItem() instanceof DyeItem dye) {
                dyes.add(dye);
            } else if (s.is(LOGO_MARKER) && !logo && !stripe) {
                logo = true;
            } else if (s.is(STRIPE_MARKER) && !logo && !stripe) {
                stripe = true;
            } else {
                return null;
            }
        }
        if (jacket.isEmpty() || dyes.isEmpty() || !(logo || stripe)) return null;
        return new Parsed(jacket, dyes, logo, stripe);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return parse(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        Parsed p = parse(container);
        if (p == null) return ItemStack.EMPTY;
        ItemStack result = p.jacket().copyWithCount(1);
        // mistura os corantes entre si (como no vanilla), sem misturar com a cor anterior da regiao
        int color = ((SabJacketItem) result.getItem()).getColor(net.minecraft.world.item.DyeableLeatherItem.dyeArmor(new ItemStack(result.getItem()), p.dyes()));
        result.getOrCreateTag().putInt(p.logo() ? SabJacketItem.LOGO_KEY : SabJacketItem.STRIPE_KEY, color);
        return result;
    }

    /** O kit nao e consumido: volta pra bancada depois da receita. */
    @Override
    public net.minecraft.core.NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        net.minecraft.core.NonNullList<ItemStack> rest = super.getRemainingItems(container);
        for (int i = 0; i < rest.size(); i++) {
            ItemStack s = container.getItem(i);
            if (s.is(LOGO_MARKER) || s.is(STRIPE_MARKER)) rest.set(i, s.copyWithCount(1));
        }
        return rest;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
