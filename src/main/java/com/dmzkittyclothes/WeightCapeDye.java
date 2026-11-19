package com.dmzkittyclothes;

import com.dragonminez.common.init.MainItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Tingimento da capa de peso do Piccolo (item do DMZ, que nao e DyeableLeatherItem): mesma cor "display.color" do couro,
 * receita de crafting (capa + corantes) e lavagem no caldeirao.
 */
public final class WeightCapeDye {

    private WeightCapeDye() {
    }

    public static boolean isWeightCape(ItemStack s) {
        return s.getItem() == MainItems.WEIGHT_PICCOLO_CAPE.get();
    }

    public static boolean hasColor(ItemStack s) {
        CompoundTag d = s.getTagElement("display");
        return d != null && d.contains("color", 99);
    }

    public static int getColor(ItemStack s) {
        CompoundTag d = s.getTagElement("display");
        return d != null && d.contains("color", 99) ? d.getInt("color") : 0xFFFFFF;
    }

    public static InteractionResult wash(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!hasColor(stack)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            ItemStack washed = stack.copyWithCount(1);
            washed.getTagElement("display").remove("color");
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (stack.isEmpty()) player.setItemInHand(hand, washed);
            else if (!player.getInventory().add(washed)) player.drop(washed, false);
            player.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static final RecipeSerializer<Recipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(Recipe::new);

    public static class Recipe extends CustomRecipe {
        public Recipe(ResourceLocation id, CraftingBookCategory category) {
            super(id, category);
        }

        private static ItemStack cape(CraftingContainer c, List<DyeItem> dyes) {
            ItemStack cape = ItemStack.EMPTY;
            for (int i = 0; i < c.getContainerSize(); i++) {
                ItemStack s = c.getItem(i);
                if (s.isEmpty()) continue;
                if (isWeightCape(s)) {
                    if (!cape.isEmpty()) return null;
                    cape = s;
                } else if (s.getItem() instanceof DyeItem d) {
                    dyes.add(d);
                } else {
                    return null;
                }
            }
            return cape.isEmpty() || dyes.isEmpty() ? null : cape;
        }

        @Override
        public boolean matches(CraftingContainer container, Level level) {
            return cape(container, new ArrayList<>()) != null;
        }

        @Override
        public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
            List<DyeItem> dyes = new ArrayList<>();
            ItemStack cape = cape(container, dyes);
            if (cape == null) return ItemStack.EMPTY;
            // mistura igual ao couro vanilla (inclusive com a cor que a capa ja tem), usando um item tingivel como molde
            ItemStack mold = new ItemStack(DmzKittyClothes.CAPES.get(0).get());
            if (hasColor(cape)) ((DyeableLeatherItem) mold.getItem()).setColor(mold, getColor(cape));
            ItemStack mixed = DyeableLeatherItem.dyeArmor(mold, dyes);
            ItemStack result = cape.copyWithCount(1);
            result.getOrCreateTagElement("display").putInt("color", ((DyeableLeatherItem) mixed.getItem()).getColor(mixed));
            return result;
        }

        @Override
        public boolean canCraftInDimensions(int w, int h) {
            return w * h >= 2;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return SERIALIZER;
        }
    }
}
