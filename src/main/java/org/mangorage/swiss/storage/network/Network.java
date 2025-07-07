package org.mangorage.swiss.storage.network;

import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public final class Network {
    private final Set<ItemDevice> itemDevices = new HashSet<>();
    private final Map<UUID, User> userMap = new HashMap<>();

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

    public void registerUser(User user) {
        userMap.put(user.getUUID(), user);
    }

    public void unregisterUser(User user) {
        userMap.remove(user.getUUID());
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
}
