package org.mangorage.swiss.storage.device;

import org.mangorage.swiss.storage.network.Network;

public interface INetworkHolder {
    Network getNetwork();

    /**
     * Tells whatever needs to getNework() wether or not it should
     * cache the result itself, or rely on getNetwork() only
     */
    boolean shouldCache();
}
