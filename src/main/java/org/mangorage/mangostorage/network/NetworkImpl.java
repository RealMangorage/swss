package org.mangorage.mangostorage.network;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class NetworkImpl implements INetwork {
    private final Set<ItemDevice> itemDevices = new HashSet<>();


    @Override
    public Stream<ItemDevice> getItemDevices() {
        return itemDevices.stream();
    }

    @Override
    public void registerDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.add(itemDevice);
    }

    public void unregisterDevice(IDevice device) {
        if (device instanceof ItemDevice itemDevice)
            itemDevices.remove(itemDevice);
    }
}
