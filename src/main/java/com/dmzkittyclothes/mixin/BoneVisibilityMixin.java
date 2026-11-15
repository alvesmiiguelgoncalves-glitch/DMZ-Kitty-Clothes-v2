package com.dmzkittyclothes.mixin;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dragonminez.client.util.BoneVisibilityHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * DMZ only hides the shirt layers (body_layer / *_arm_layer) when a vanilla chestplate is worn, so a Curios jacket
 * lets the DMZ shirt poke through. After DMZ updates the visibility, hide them when the jacket is equipped.
 */
@Mixin(value = BoneVisibilityHandler.class, remap = false)
public abstract class BoneVisibilityMixin {

    @Inject(method = "updateVisibility", at = @At("TAIL"), remap = false)
    private static void kitty$hideShirt(BakedGeoModel model, AbstractClientPlayer player,
                                        GeoRenderLayer<?> renderLayer, CallbackInfo ci) {
        boolean worn = CuriosApi.getCuriosInventory(player)
                .map(h -> h.findFirstCurio(DmzKittyClothes.SAB_JACKET.get()).isPresent())
                .orElse(false);
        if (!worn) return;
        BoneVisibilityHandler.hideBone(model, "body_layer", true);
        BoneVisibilityHandler.hideBone(model, "right_arm_layer", true);
        BoneVisibilityHandler.hideBone(model, "left_arm_layer", true);
    }
}
