package com.dmzkittyclothes.mixin;

import com.dragonminez.client.render.layer.DMZWeightCapeLayer;
import com.dragonminez.common.init.item.WeightItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * A capa de peso do Piccolo do DMZ passa a ser desenhada pela capa deste mod (CapeRenderer, registrada no Curios).
 * Aqui o DMZ so deixa de desenhar a capa dele, para nao aparecerem duas.
 */
@Mixin(value = DMZWeightCapeLayer.class, remap = false)
public abstract class WeightCapeLayerMixin {

    @Inject(method = "getArmorItemForBone", at = @At("RETURN"), cancellable = true, remap = false)
    private void kitty$hideDmzCape(GeoBone bone, net.minecraft.client.player.AbstractClientPlayer animatable, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack != null && stack.getItem() instanceof WeightItem w && w.getWeightType() == WeightItem.WeightType.PICCOLO_CAPE) {
            cir.setReturnValue(null);
        }
    }
}
