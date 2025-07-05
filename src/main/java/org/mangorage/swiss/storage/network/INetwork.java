package org.mangorage.swiss.storage.network;

import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;

import java.util.stream.Stream;

public interface INetwork {
    Stream<ItemDevice> getItemDevices();

    void registerDevice(IDevice device);
    void unregisterDevice(IDevice device);
}
