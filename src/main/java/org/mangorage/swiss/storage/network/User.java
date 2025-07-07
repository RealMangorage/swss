package org.mangorage.swiss.storage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class User {
    public static final StreamCodec<FriendlyByteBuf, User> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, u -> u.getUUID().toString(),
            ByteBufCodecs.<ByteBuf, Permission>list().apply(Permission.STREAM_CODEC), u -> u.permissions.stream().toList(),
            (uuid, list) -> new User(uuid, new HashSet<>(list))
    );

    /**
     * Permissions that this user has.
     */
    private final Set<Permission> permissions;
    private final UUID uuid;

    public User(UUID uuid) {
        this.permissions = new HashSet<>();
        this.uuid = uuid;
    }

    User(String uuid, Set<Permission> permissions) {
        this.uuid = UUID.fromString(uuid);
        this.permissions = permissions;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean hasPermission(Set<Permission> permissions) {
        for (Permission permission : permissions) {
            if (hasPermission(permission))
                return true;
        }
        return false;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        final var permissionsTag = new ListTag();
        for (Permission permission : permissions) {
            permissionsTag.add(StringTag.valueOf(permission.name()));
        }
        compoundTag.put("permissions", permissionsTag);
        return compoundTag;
    }
}
