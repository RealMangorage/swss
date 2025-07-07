package org.mangorage.swiss.storage.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public sealed class Network permits UnknownNetwork {
    private final Set<ItemDevice> itemDevices = new HashSet<>();
    private final Map<UUID, User> userMap = new HashMap<>();
    private boolean dirty = true;

    public Network(UUID owner) {
        final var ownerUser = new User(owner);
        ownerUser.addPermission(Permission.OWNER);
        this.userMap.put(owner, ownerUser);
    }

    public boolean hasPermission(UUID userId, Set<Permission> permissions) {
        final var user = userMap.get(userId);
        if (user == null) return false;
        return user.hasPermission(permissions);
    }

    public void addPermission(UUID userId, Set<Permission> permissions) {
        final var user = userMap.get(userId);
        if (user == null || permissions.isEmpty()) return;
        for (Permission permission : permissions) {
            user.addPermission(permission);
        }

        dirty = true;
    }

    public void removePermission(UUID uuid, Set<Permission> permissions) {
        final var user = userMap.get(uuid);
        if (user == null || permissions.isEmpty()) return;
        for (Permission permission : permissions) {
            user.removePermission(permission);
        }

        dirty = true;
    }

    public void registerUser(User user) {
        userMap.put(user.getUUID(), user);
        dirty = true;
    }

    public void unregisterUser(User user) {
        userMap.remove(user.getUUID());
        dirty = true;
    }

    public void registerDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.add(itemDevice);
    }

    public void unregisterDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.remove(itemDevice);
    }

    public Stream<ItemDevice> getItemDevices() {
        return itemDevices.stream();
    }

    public boolean isDirty() {
        return dirty;
    }

    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        dirty = false;
        userMap.forEach((id, user) -> {
            compoundTag.put(id.toString(), user.save(new CompoundTag(), registries));
        });
        return compoundTag;
    }
}
