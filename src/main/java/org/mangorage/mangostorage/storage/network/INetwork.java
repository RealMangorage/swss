package org.mangorage.mangostorage.storage.network;

import org.mangorage.mangostorage.storage.device.IDevice;
import org.mangorage.mangostorage.storage.device.ItemDevice;

import java.util.stream.Stream;

public interface INetwork {
    Stream<ItemDevice> getItemDevices();

    void registerDevice(IDevice device);
    void unregisterDevice(IDevice device);
}
