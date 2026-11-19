package com.dmzkittyclothes.client;

import com.dmzkittyclothes.DmzKittyClothes;
import com.dmzkittyclothes.AccessoryItem;
import com.dragonminez.client.util.SaiyanTailRules;
import com.dragonminez.common.init.armor.DbzArmorCapeItem;
import com.dragonminez.common.init.item.WeightItem;
import com.dragonminez.common.network.C2S.ExecuteActionC2S;
import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Atalho para amarrar/soltar a cauda (usa a mesma acao do menu radial do DMZ, TOGGLE_TAIL) e amarra a cauda
 * sozinha quando uma capa e equipada. Soltar de novo continua manual.
 */
public final class TailKeys {

    public static final KeyMapping TOGGLE_TAIL = new KeyMapping("key.dmz_kitty_clothes.toggle_tail",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.dmz_kitty_clothes");

    private TailKeys() {
    }

    @Mod.EventBusSubscriber(modid = DmzKittyClothes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModEvents {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_TAIL);
        }
    }

    @Mod.EventBusSubscriber(modid = DmzKittyClothes.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {
        private static boolean hadCape;

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                hadCape = false;
                return;
            }
            StatsData stats = StatsProvider.get(StatsCapability.INSTANCE, player).orElse(null);
            if (stats == null) return;

            while (TOGGLE_TAIL.consumeClick()) {
                if (SaiyanTailRules.hasTail(stats.getCharacter())) {
                    NetworkHandler.sendToServer(new ExecuteActionC2S(ExecuteActionC2S.ActionType.TOGGLE_TAIL));
                }
            }

            boolean cape = wearsCape(player);
            // capa acabou de ser equipada: se a cauda esta solta, amarra
            if (cape && !hadCape && SaiyanTailRules.hasTail(stats.getCharacter()) && stats.getStatus().isTailVisible()) {
                NetworkHandler.sendToServer(new ExecuteActionC2S(ExecuteActionC2S.ActionType.TOGGLE_TAIL));
            }
            hadCape = cape;
        }

        private static boolean wearsCape(LocalPlayer player) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof DbzArmorCapeItem) return true;
            return CuriosApi.getCuriosInventory(player).map(inv -> {
                for (var entry : inv.getCurios().entrySet()) {
                    var stacks = entry.getValue().getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack s = stacks.getStackInSlot(i);
                        if (s.isEmpty()) continue;
                        if (s.getItem() instanceof WeightItem w && w.getWeightType() == WeightItem.WeightType.PICCOLO_CAPE) return true;
                        if (entry.getKey().equals("cape") && s.getItem() instanceof AccessoryItem) return true;
                    }
                }
                return false;
            }).orElse(false);
        }
    }
}
