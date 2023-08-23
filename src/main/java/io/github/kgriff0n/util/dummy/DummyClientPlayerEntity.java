package io.github.kgriff0n.util.dummy;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

//Credits: https://github.com/enjarai/show-me-your-skin
public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;
    private Identifier capeIdentifier = null;
    private String model = null;
    private PlayerEntity player = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity() {
            @Override
            public Text getName() {
                return Text.translatable("gui.showmeyourskin.armorScreen.global");
            }
        };
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        setUuid(UUID.randomUUID());
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                skinIdentifier = identifier;
                model = texture.getMetadata("model");
                if (model == null) {
                    model = "default";
                }
            }
        }, true);
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, Identifier skinIdentifier, @Nullable Identifier capeIdentifier, @Nullable String model) {
        this(player, uuid, skinIdentifier, capeIdentifier, model, DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance());
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, Identifier skinIdentifier, @Nullable Identifier capeIdentifier, @Nullable String model, ClientWorld world, ClientPlayNetworkHandler networkHandler) {
        super(MinecraftClient.getInstance(), world, networkHandler, null, null,false, false);
        this.player = player;
        setUuid(uuid);
        this.skinIdentifier = skinIdentifier;
        this.capeIdentifier = capeIdentifier;
        this.model = model;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        return skinIdentifier == null ? DefaultSkinHelper.getTexture(getUuid()) : skinIdentifier;
    }

    @Override
    public boolean canRenderCapeTexture() {
        return capeIdentifier != null;
    }

    @Nullable
    @Override
    public Identifier getCapeTexture() {
        return capeIdentifier;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public String getModel() {
        return model == null ? DefaultSkinHelper.getModel(getUuid()) : model;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (player != null) {
            return player.getEquippedStack(slot);
        }
        return equippedStackSupplier.apply(slot);
    }
}
