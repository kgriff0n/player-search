package io.github.kgriff0n.screen;

import com.mojang.authlib.GameProfile;
import io.github.kgriff0n.PlayerSearch;
import io.github.kgriff0n.util.GuiEntityRenderer;
import io.github.kgriff0n.util.PlayerApi;
import io.github.kgriff0n.util.dummy.DummyClientPlayerEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.kgriff0n.PlayerSearch.mc;

@SuppressWarnings("ConstantConditions")
public class PlayerScreen extends Screen {

    protected final Screen parent;
    protected GameProfile profile;
    private boolean firstLoad = true;

    private EditBoxWidget editBox;

    private String previousText;
    private String playerName = Text.translatable("gui.player_search.unknown").getString();
    private UUID playerUuid;
    private JSONArray nameHistory;
    private DummyClientPlayerEntity dummyClientPlayerEntity;
    private SkinTextures skinTextures;

    public PlayerScreen(Screen parent, @Nullable GameProfile profile) {
        super(Text.translatable("playerSearch.title"));
        this.parent = parent;
        this.profile = profile;
    }

    @Override
    protected void init() {
        this.previousText = "";
        this.editBox = this.addDrawableChild(new EditBoxWidget(this.textRenderer, this.width / 84, this.height / 84, 300, 20, Text.translatable("gui.player_search.search_hint"), Text.literal("")));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.player_search.button"), button -> {
            String text = this.editBox.getText();
            if (text == this.previousText) return;
            JSONObject request;
            if (text != "") {
                this.previousText = text;
                if (text.length() == 32 || text.length() == 36) {
                    request = PlayerApi.getProfileFromUUID(text);
                } else {
                    request = PlayerApi.getProfileFromName(text);
                }
                if (request == null) {
                    this.skinTextures = null;
                    this.playerName = Text.translatable("gui.player_search.unknown").getString();
                    this.playerUuid = null;
                    this.nameHistory = null;
                } else {
                    this.playerUuid = UUID.fromString(parseUuid(request.get("id").toString()));
                    this.playerName = request.get("name").toString();
                    CompletableFuture.runAsync(() -> {
                        this.nameHistory = PlayerApi.getNameHistory(this.playerUuid.toString());
                    });
                    this.profile = new GameProfile(this.playerUuid, this.playerName);
                    loadSKin(this.profile);
                }
            }
        }).dimensions(330, this.height / 84, textRenderer.getWidth("Search") + 10, 20).build());
        if (this.firstLoad && this.profile != null) {
            this.playerUuid = this.profile.getId();
            this.playerName = this.profile.getName();
            this.editBox.setText(this.playerName);
            this.previousText = this.playerName;
            CompletableFuture.runAsync(() -> this.nameHistory = PlayerApi.getNameHistory(this.playerUuid.toString()));
            loadSKin(this.profile);
            this.firstLoad = false;
        }
    }

    private void loadSKin(GameProfile profile) {
        PlayerSearch.LOGGER.info("Request sent to Mojang API...");
        mc.getSkinProvider().fetchSkinTextures(profile).thenAccept((textures) -> {
            skinTextures = textures;
        });
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int playerX = this.width / 12;
        int playerY = this.height / 2;
        int playerSize = this.height / 5;
        context.fill(playerX - this.width / 14, playerY - playerSize*2, playerX + this.width / 14, playerY + 10, 0x80000000);
        this.dummyClientPlayerEntity = new DummyClientPlayerEntity(null, UUID.fromString("fffffff0-ffff-fff0-ffff-fff0ffffffff"), this.skinTextures);
        GuiEntityRenderer.drawEntity(context.getMatrices(), playerX, playerY, playerSize, 0, playerX - mouseX, playerY - playerSize*1.8 - mouseY, this.dummyClientPlayerEntity);

        int scaleFactor = this.width / 200;
        float reverseScaleFactor = (float) (1.0 / scaleFactor);

        context.getMatrices().scale(scaleFactor, scaleFactor, 1);
        context.drawText(this.textRenderer, Text.literal(this.playerName), (2*playerX + 20) / scaleFactor, (this.height / 10) / scaleFactor, 0xFFFFFFFF, true);
        context.getMatrices().scale(reverseScaleFactor, reverseScaleFactor , 1);
        if (this.playerUuid != null) {
            context.drawText(this.textRenderer, Text.literal(this.playerUuid.toString()), 2*playerX + 20, (this.height / 10) + 50, 0xFFFFFF, true);
        }

        if (this.nameHistory != null && this.nameHistory.size() != 0) {
            context.drawText(this.textRenderer, Text.translatable("gui.player_search.name_history").formatted(Formatting.UNDERLINE), this.width * 2 / 3, (this.height / 10) + 20, 0xFFFFFF, true);
            this.drawNameHistory(context, this.nameHistory, this.width * 2 / 3, this.height / 10 + 40);
        }
    }

    private void drawNameHistory(DrawContext context, JSONArray history, int x, int y) {
        int width = 200;
        int number = 0;
        for (Object object : history) {
            if (number%2 == 0) {
                context.fill(x, y, x+width, y+15, 0x80000000);
            } else {
                context.fill(x, y, x+width, y+14, 0x80646464);
            }

            JSONObject jsonObject = (JSONObject) object;
            String username = (String) jsonObject.get("username");
            Object date = jsonObject.get("changed_at");

            if (date == null) {
                date = "                      ";
            } else if (date.toString().length() > 10) {
                date = date.toString().substring(0, 10).replace("-", "/") + "   -   ";
            } else {
                date = date.toString().replace("-", "/") + "   -   ";
            }

            context.drawText(this.textRenderer, Text.literal(date + username), x, y+4, 0xFFFFFF, false);
            number += 1;
            y += 15;
        }
    }

    private static String parseUuid(String uuid) {
        StringBuilder stringBuilder = new StringBuilder(uuid);
        if (uuid.length() == 32) {
            stringBuilder.insert(8, '-');
            stringBuilder.insert(13, '-');
            stringBuilder.insert(18, '-');
            stringBuilder.insert(23, '-');
        }
        return stringBuilder.toString();
    }
}
