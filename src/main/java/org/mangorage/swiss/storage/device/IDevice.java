package org.mangorage.swiss.storage.device;

public interface IDevice {
    int getNetworkId();
    void setNetworkId(int id);

    boolean isValidDevice();
}
