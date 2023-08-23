package io.github.kgriff0n.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import io.github.kgriff0n.util.PlayerApi;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.xpple.clientarguments.arguments.CGameProfileArgumentType.gameProfile;
import static dev.xpple.clientarguments.arguments.CGameProfileArgumentType.getCProfileArgument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class NameHistoryCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("name-history")
                .then(argument("player", gameProfile())
                        .executes(ctx -> nameHistory(ctx.getSource(), getCProfileArgument(ctx, "player")))));
    }

    private static int nameHistory(FabricClientCommandSource source, Collection<GameProfile> profiles) {
        CompletableFuture.runAsync(() -> {
        MinecraftClient client = source.getClient();
        if (profiles.size() > 1) {
            client.player.sendMessage(Text.translatable("argument.player.toomany").formatted(Formatting.RED));
            return;
        }

        GameProfile profile = profiles.iterator().next();
        UUID uuid = profile.getId();
        JSONArray nameHistory = PlayerApi.getNameHistory(uuid.toString());
        if (nameHistory.size() == 0) {
            client.player.sendMessage(Text.translatable("argument.player.unknown").formatted(Formatting.RED));
            return;
        }
        for (Object object : nameHistory) {
            JSONObject jsonObject = (JSONObject) object;
            String username = (String) jsonObject.get("username");
            Object date = jsonObject.get("changed_at");
            if (date != null) {
                date = date.toString().substring(0, 10).replace("-", "/") + "   -   ";
            } else {
                date = "                      ";
            }
            client.player.sendMessage(Text.literal((String) date).formatted(Formatting.GRAY).append(Text.literal(username).formatted(Formatting.AQUA)));
        }});
        return Command.SINGLE_SUCCESS;
    }
}
