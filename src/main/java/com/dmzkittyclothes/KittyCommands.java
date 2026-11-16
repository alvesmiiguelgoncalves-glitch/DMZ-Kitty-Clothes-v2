package com.dmzkittyclothes;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * /kittycolor <main|second|third> <hex>  e  /kittycolor clear  -  define a cor exata do item na mao principal
 * (precisa de permissao de operador/cheats). second e third so existem na jaqueta.
 */
@Mod.EventBusSubscriber(modid = DmzKittyClothes.MOD_ID)
public final class KittyCommands {

    private KittyCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("kittycolor")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("clear").executes(KittyCommands::clear))
                .then(Commands.argument("slot", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(List.of("main", "second", "third"), b))
                        .then(Commands.argument("hex", StringArgumentType.word()).executes(KittyCommands::set))));
    }

    private static ItemStack held(CommandContext<CommandSourceStack> c) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        ItemStack s = p.getMainHandItem();
        return s.getItem() instanceof DyeableLeatherItem && (s.getItem() instanceof SabJacketItem || s.getItem() instanceof GlovesItem || s.getItem() instanceof SuitItem)
                ? s : ItemStack.EMPTY;
    }

    private static int clear(CommandContext<CommandSourceStack> c) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ItemStack s = held(c);
        if (s.isEmpty()) {
            c.getSource().sendFailure(Component.translatable("command.kittycolor.hold"));
            return 0;
        }
        SabJacketItem.clearAllColors(s);
        if (!(s.getItem() instanceof SabJacketItem)) ((DyeableLeatherItem) s.getItem()).clearColor(s);
        c.getSource().sendSuccess(() -> Component.translatable("command.kittycolor.cleared"), false);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> c) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ItemStack s = held(c);
        if (s.isEmpty()) {
            c.getSource().sendFailure(Component.translatable("command.kittycolor.hold"));
            return 0;
        }
        String slot = StringArgumentType.getString(c, "slot");
        String hex = StringArgumentType.getString(c, "hex").replace("#", "");
        int color;
        try {
            if (hex.length() != 6) throw new NumberFormatException();
            color = Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            c.getSource().sendFailure(Component.translatable("command.kittycolor.hex"));
            return 0;
        }
        switch (slot) {
            case "main" -> ((DyeableLeatherItem) s.getItem()).setColor(s, color);
            case "second", "third" -> {
                if (!(s.getItem() instanceof SabJacketItem)) {
                    c.getSource().sendFailure(Component.translatable("command.kittycolor.slot"));
                    return 0;
                }
                s.getOrCreateTag().putInt(slot.equals("second") ? SabJacketItem.LOGO_KEY : SabJacketItem.STRIPE_KEY, color);
            }
            default -> {
                c.getSource().sendFailure(Component.translatable("command.kittycolor.slot"));
                return 0;
            }
        }
        c.getSource().sendSuccess(() -> Component.translatable("command.kittycolor.ok", slot, hex.toUpperCase()), false);
        return 1;
    }
}
