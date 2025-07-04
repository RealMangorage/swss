package org.mangorage.mangostorage.network;

import java.util.stream.Stream;

public interface INetwork {
    Stream<ItemDevice> getItemDevices();

    void registerDevice(IDevice device);
    void unregisterDevice(IDevice device);
}
