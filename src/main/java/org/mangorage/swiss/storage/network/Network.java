package org.mangorage.swiss.storage.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
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
    private final UUID networkId;

    private String networkName = null;

    private boolean dirty = true;

    public Network(UUID networkId) {
        this.networkId = networkId;
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

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
        dirty = true;
    }

    public UUID getId() {
        return networkId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void registerUser(User user) {
        userMap.put(user.getUUID(), user);
        dirty = true;
    }

    public void unregisterUser(User user) {
        userMap.remove(user.getUUID());
        dirty = true;
    }

    public boolean hasUser(UUID userId) {
        return userMap.containsKey(userId);
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

    public CompoundTag save(CompoundTag network, HolderLookup.Provider registries) {
        dirty = false;

        ListTag userTags = new ListTag();
        userMap.forEach((id, user) -> {
            userTags.add(user.save(new CompoundTag(), registries));
        });

        network.put("users", userTags);
        network.putString("name", networkName);

        return network;
    }

    public void load(CompoundTag networkTag, HolderLookup.Provider registries) {
        final var userTags = networkTag.getList("users", ListTag.TAG_COMPOUND);
        for (int id = 0; id < userTags.size(); id++) {
            final var userTag = userTags.getCompound(id);
            final var user = new User(userTag.getUUID("id"));
            user.load(userTag, registries);
            userMap.put(user.getUUID(), user);
        }
        this.networkName = networkTag.getString("name");
    }

    public NetworkInfo getInfo(ServerPlayer player) {
        return new NetworkInfo(networkName, getId(), hasUser(player.getUUID()));
    }
}
