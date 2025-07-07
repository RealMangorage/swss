package org.mangorage.swiss.storage.device;

import java.util.UUID;

public interface IDevice {
    UUID getNetworkId();
    void setNetworkId(UUID id);

    void setOwner(UUID uuid);
    UUID getOwner();

    boolean isValidDevice();
}
