package org.mangorage.swiss.storage.device;

import java.util.UUID;

public interface IDevice {
    UUID getNetworkId();
    void setNetwork(UUID id);

    void setOwner(UUID uuid);
    UUID getOwner();

    boolean isValidDevice();
}
