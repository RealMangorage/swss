package org.mangorage.swiss.storage.network;

import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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


    public Stream<ItemDevice> getItemDevices() {
        return itemDevices.stream();
    }

    public void registerDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.add(itemDevice);
    }

    public void unregisterDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.remove(itemDevice);
    }
}
