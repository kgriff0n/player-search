package io.github.kgriff0n;

import io.github.kgriff0n.command.NameHistoryCommand;
import io.github.kgriff0n.command.SearchCommand;
import io.github.kgriff0n.util.dummy.DummyClientPlayerEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerSearch implements ClientModInitializer {

	public static final String MOD_ID = "player-search";
    public static final Logger LOGGER = LoggerFactory.getLogger("player-search");

	public static MinecraftClient mc;

	@Override
	public void onInitializeClient() {
		mc = MinecraftClient.getInstance();

		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> SearchCommand.register(dispatcher)));
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> NameHistoryCommand.register(dispatcher)));

		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> DummyClientPlayerEntity.getInstance());
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
