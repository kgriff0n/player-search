package io.github.kgriff0n;

import io.github.kgriff0n.command.NameHistoryCommand;
import io.github.kgriff0n.command.SearchCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerSearch implements ModInitializer {

	public static final String MOD_ID = "player-search";
    public static final Logger LOGGER = LoggerFactory.getLogger("player-search");

	public static MinecraftClient mc;

	@Override
	public void onInitialize() {
		mc = MinecraftClient.getInstance();

		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> SearchCommand.register(dispatcher)));
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> NameHistoryCommand.register(dispatcher)));
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
