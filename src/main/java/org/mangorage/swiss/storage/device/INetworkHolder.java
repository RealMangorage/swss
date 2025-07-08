package org.mangorage.swiss.storage.device;

import org.mangorage.swiss.storage.network.Network;

import java.util.UUID;

public interface INetworkHolder {
    Network getNetwork();
    void setNetwork(UUID id);

    /**
     * Tells whatever that it needs cache getNetwork()
     */
    boolean shouldCache();
}
