package com.dmzkittyclothes.mixin;

import com.dmzkittyclothes.client.pose.KittyPoseSync;
import com.dragonminez.client.render.VanillaModelSync;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * DMZ's {@code VanillaModelSync.sync} copies the animated GeckoLib pose into the vanilla player model that
 * third-party layers (Curios clothes) are drawn with, but the copy is approximate: the arm's X position has the
 * wrong sign and the waist rotation is added to the arm rotation instead of composed. Clothes then do not follow
 * the real body.
 * <p>
 * We let DMZ run and then overwrite the six poses with the exact ones. If DMZ fixes this itself, the result is the
 * same and this mixin can be removed.
 */
@Mixin(value = VanillaModelSync.class, remap = false)
public abstract class VanillaModelSyncMixin {

    @Inject(method = "sync", at = @At("TAIL"), remap = false)
    private static void kitty$exactSync(BakedGeoModel geoModel, PlayerModel<AbstractClientPlayer> vanillaModel,
                                        AbstractClientPlayer player, CallbackInfo ci) {
        KittyPoseSync.apply(geoModel, vanillaModel);
    }
}
