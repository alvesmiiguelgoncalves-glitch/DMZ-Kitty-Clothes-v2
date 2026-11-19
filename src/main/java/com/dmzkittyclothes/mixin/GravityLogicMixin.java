package com.dmzkittyclothes.mixin;

import com.dragonminez.common.init.item.WeightItem;
import com.dragonminez.server.util.GravityLogic;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * O DMZ so soma o peso dos itens no slot "weights". A capa de peso tambem pode ir no slot Costas ("cape"),
 * entao somamos aqui o peso dos itens de peso que estiverem nele.
 */
@Mixin(value = GravityLogic.class, remap = false)
public abstract class GravityLogicMixin {

    @Inject(method = "getTotalWeight", at = @At("RETURN"), cancellable = true, remap = false)
    private static void kitty$capeSlotWeight(Player player, CallbackInfoReturnable<Integer> cir) {
        int extra = CuriosApi.getCuriosInventory(player).map(inv -> {
            var handler = inv.getCurios().get("cape");
            if (handler == null) return 0;
            int sum = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStacks().getStackInSlot(i);
                if (stack.getItem() instanceof WeightItem) sum += WeightItem.getWeight(stack);
            }
            return sum;
        }).orElse(0);
        if (extra != 0) cir.setReturnValue(cir.getReturnValueI() + extra);
    }
}
