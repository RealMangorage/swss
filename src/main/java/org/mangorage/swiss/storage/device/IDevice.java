package org.mangorage.swiss.storage.device;

import java.util.UUID;

public interface IDevice {
    int getNetworkId();
    void setNetworkId(int id);

    void setOwner(UUID uuid);
    UUID getOwner();

    boolean isValidDevice();
}
