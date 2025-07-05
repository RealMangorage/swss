package org.mangorage.mangostorage.storage.device;

public interface IDevice {
    int getNetworkId();
    void setNetworkId(int id);

    boolean isValidDevice();
}
