package com.bronzeisunbreakable.easywlforge.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

import java.util.Collection;
import java.util.Collections;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class EasyWhitelistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED =
            new SimpleCommandExceptionType(Component.translatable("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED =
            new SimpleCommandExceptionType(Component.translatable("commands.whitelist.remove.failed"));

    /**
     * Registro de comando encargado del uso de la whitelist
     *
     * @param dispatcher Dispatcher conteniendo el stack de comandos del server
     */
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wl")
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", word())
                                .executes(ctx ->
                                        addPlayers(ctx.getSource(),
                                                getProfileFromNickname(
                                                        getString(ctx, "targets")
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", word())
                                .executes(ctx ->
                                        removePlayers(
                                                ctx.getSource(),
                                                getProfileFromNickname(
                                                        getString(ctx, "targets")
                                                )
                                        )
                                )
                        )
                )
        );
    }

    /**
     * Función que agrega jugadores a la whitelist utilizando la generación de UUID's offline
     *
     * @param commandSourceStack Stack de comandos
     * @param gameProfiles       Perfil de jugador a ser agregado
     * @return Estado de salida
     * @throws CommandSyntaxException En caso que el jugador ya esté registrado
     */
    private static int addPlayers(
            CommandSourceStack commandSourceStack,
            Collection<GameProfile> gameProfiles
    ) throws CommandSyntaxException {
        UserWhiteList userwhitelist = commandSourceStack.getServer().getPlayerList().getWhiteList();
        int i = 0;

        for (GameProfile gameprofile : gameProfiles) {
            if (!userwhitelist.isWhiteListed(gameprofile)) {
                UserWhiteListEntry userwhitelistentry = new UserWhiteListEntry(gameprofile);
                userwhitelist.add(userwhitelistentry);
                commandSourceStack.sendSuccess(
                        () -> Component.translatable(
                                "commands.whitelist.add.success",
                                ComponentUtils.getDisplayName(gameprofile)
                        ),
                        true
                );
                ++i;
            }
        }

        if (i == 0) {
            throw ERROR_ALREADY_WHITELISTED.create();
        } else {
            return i;
        }
    }

    /**
     * Función que remueve jugadores a la whitelist utilizando la generación de UUID's offline
     *
     * @param commandSourceStack Stack de comandos
     * @param gameProfiles       Perfil de jugador a ser agregado
     * @return Estado de salida
     * @throws CommandSyntaxException En caso que el jugador no esté registrado
     */
    private static int removePlayers(
            CommandSourceStack commandSourceStack,
            Collection<GameProfile> gameProfiles
    ) throws CommandSyntaxException {
        UserWhiteList userwhitelist = commandSourceStack.getServer().getPlayerList().getWhiteList();
        int i = 0;

        for (GameProfile gameprofile : gameProfiles) {
            if (userwhitelist.isWhiteListed(gameprofile)) {
                UserWhiteListEntry userwhitelistentry = new UserWhiteListEntry(gameprofile);
                userwhitelist.remove(userwhitelistentry);
                commandSourceStack.sendSuccess(
                        () -> Component.translatable(
                                "commands.whitelist.remove.success",
                                ComponentUtils.getDisplayName(gameprofile)
                        ),
                        true
                );
                ++i;
            }
        }

        if (i == 0) {
            throw ERROR_NOT_WHITELISTED.create();
        } else {
            commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
            return i;
        }
    }

    /**
     * Función que obtiene el perfil de un jugador con su UUID generado de manera offline
     *
     * @param nickname Nickname del jugador
     * @return Perfil de jugador
     */
    private static Collection<GameProfile> getProfileFromNickname(String nickname) {
        return Collections.singletonList(new GameProfile(UUIDUtil.createOfflinePlayerUUID(nickname), nickname));
    }
}
