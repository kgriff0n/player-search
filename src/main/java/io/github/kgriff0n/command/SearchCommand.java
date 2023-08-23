package io.github.kgriff0n.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.kgriff0n.screen.PlayerScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static dev.xpple.clientarguments.arguments.CGameProfileArgumentType.gameProfile;
import static dev.xpple.clientarguments.arguments.CGameProfileArgumentType.getCProfileArgument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SearchCommand {

    private static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.player.toomany"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("search")
                .executes(ctx -> openScreen(ctx.getSource(), null))
                .then(argument("player", gameProfile())
                        .executes(ctx -> openScreen(ctx.getSource(), getCProfileArgument(ctx, "player")))));
    }

    private static int openScreen(FabricClientCommandSource source, @Nullable Collection<GameProfile> profiles) throws CommandSyntaxException {
        MinecraftClient client = source.getClient();
        if (profiles != null) {
            if (profiles.size() > 1) {
                throw TOO_MANY_PLAYERS_EXCEPTION.create();
            }
            client.send(() -> client.setScreen(new PlayerScreen(client.currentScreen, profiles.iterator().next())));
        } else {
            client.send(() -> client.setScreen(new PlayerScreen(client.currentScreen, null)));
        }
        return Command.SINGLE_SUCCESS;
    }
}
