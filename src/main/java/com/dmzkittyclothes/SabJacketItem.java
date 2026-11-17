package com.dmzkittyclothes;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Curios item (slot "jacket") com tres regioes tingiveis:
 * <ul>
 *   <li>principal: cor do vanilla (display.color), qualquer corante na bancada, como armadura de couro;</li>
 *   <li>logo: corante + ametista na bancada (NBT "logoColor");</li>
 *   <li>listra: corante + quartzo na bancada (NBT "stripeColor").</li>
 * </ul>
 * Regiao sem corante usa a cor original da textura. O caldeirao com agua tira as tres cores.
 */
public class SabJacketItem extends Item implements ICurioItem, DyeableLeatherItem, MultiColor {

    @Override
    public boolean hasThird() {
        return true;
    }

    public static final String LOGO_KEY = "logoColor";
    public static final String STRIPE_KEY = "stripeColor";

    public SabJacketItem(Properties properties) {
        super(properties);
    }

    /** Botao direito com a jaqueta na mao equipa no slot de Curios (como armadura). */
    @Override
    public boolean canEquipFromUse(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        return true;
    }

    public static boolean hasLogoColor(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(LOGO_KEY, 99);
    }

    public static int getLogoColor(ItemStack stack) {
        return stack.getTag().getInt(LOGO_KEY);
    }

    public static boolean hasStripeColor(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(STRIPE_KEY, 99);
    }

    public static int getStripeColor(ItemStack stack) {
        return stack.getTag().getInt(STRIPE_KEY);
    }

    public static boolean hasAnyColor(ItemStack stack) {
        return stack.getItem() instanceof DyeableLeatherItem item && (item.hasCustomColor(stack) || hasLogoColor(stack) || hasStripeColor(stack));
    }

    public static void clearAllColors(ItemStack stack) {
        if (stack.getItem() instanceof DyeableLeatherItem item) item.clearColor(stack);
        if (stack.getTag() != null) {
            stack.getTag().remove(LOGO_KEY);
            stack.getTag().remove(STRIPE_KEY);
        }
    }

    /** Caldeirao com agua: remove as tres cores (o DYED_ITEM do vanilla so remove a principal). */
    public static InteractionResult washInCauldron(BlockState state, Level level, BlockPos pos, Player player,
                                                   InteractionHand hand, ItemStack stack) {
        if (!hasAnyColor(stack)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            ItemStack washed = stack.copyWithCount(1);
            clearAllColors(washed);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (stack.isEmpty()) player.setItemInHand(hand, washed);
            else if (!player.getInventory().add(washed)) player.drop(washed, false);
            player.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
