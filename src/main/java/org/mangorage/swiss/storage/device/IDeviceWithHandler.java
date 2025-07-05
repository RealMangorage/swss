package org.mangorage.swiss.storage.device;

public interface IDeviceWithHandler extends IDevice {
    boolean canInsert(DeviceType type);
    boolean canExtract(DeviceType type);
}
