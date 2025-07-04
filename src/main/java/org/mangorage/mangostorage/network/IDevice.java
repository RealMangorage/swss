package org.mangorage.mangostorage.network;

public interface IDevice {
    int getNetworkId();
    void setNetworkId(int id);

    boolean isValidDevice();
}
