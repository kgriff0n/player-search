package io.github.kgriff0n.util.dummy;

import com.mojang.datafixers.util.Either;
import io.github.kgriff0n.PlayerSearch;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

//Credits: https://github.com/enjarai/show-me-your-skin
public record CursedRegistryEntry<T>(T value, RegistryKey<? extends Registry<T>> key) implements RegistryEntry<T> {
    @Override
    public boolean hasKeyAndValue() {
        return true;
    }

    @Override
    public boolean matchesId(Identifier id) {
        return false;
    }

    @Override
    public boolean matchesKey(RegistryKey<T> key) {
        return false;
    }

    @Override
    public boolean isIn(TagKey<T> tag) {
        return false;
    }

    @Override
    public boolean matches(Predicate<RegistryKey<T>> predicate) {
        return false;
    }

    @Override
    public Either<RegistryKey<T>, T> getKeyOrValue() {
        return Either.right(this.value);
    }

    @Override
    public Optional<RegistryKey<T>> getKey() {
        return Optional.of(RegistryKey.of(key, PlayerSearch.id("dummy")));
    }

    @Override
    public Type getType() {
        return Type.DIRECT;
    }

    @Override
    public String toString() {
        return "CursedRegistryEntry(THIS IS FROM PLAYER SEARCH, SORRY IN ADVANCE FOR ANY ISSUES CAUSED){" + this.value + "}";
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        return true;
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return Stream.of();
    }
}
