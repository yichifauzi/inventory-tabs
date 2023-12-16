package folk.sisby.inventory_tabs.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public record RegistryValue<T>(Either<RegistryEntry<T>, TagKey<T>> value) {
    public Either<T, TagKey<T>> getValue() {
        return this.value.mapLeft(RegistryEntry::comp_349);
    }

    public boolean isTag() {
        return this.value.right().isPresent();
    }

    public static @Nullable <T> RegistryValue<T> fromRegistryString(DynamicRegistryManager manager, RegistryKey<? extends Registry<T>> registry, String value) {
        if (value.startsWith("#")) {
            Identifier tagId = Identifier.tryParse(value.substring(1));
            return tagId != null ? new RegistryValue<>(Either.right(TagKey.of(registry, tagId))) : null;
        } else {
            Identifier id = Identifier.tryParse(value);
            if (id == null) return null;

            return manager.get(registry).getEntry(RegistryKey.of(registry, id)).map(h -> new RegistryValue<>(Either.left(h))).orElse(null);
        }
    }

    public String toRegistryString() {
        return value
                .map(
                        holder -> holder.getKey()
                                .map(RegistryKey::getValue)
                                .map(Identifier::toString)
                                .orElse(null),
                        tag -> "#" + tag.id()
                );
    }

    public boolean is(RegistryEntry<T> value) {
        return this.value.map((v) -> v.equals(value), value::isIn);
    }
}
