package org.mangorage.swiss;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.server.MinecraftServer;
import org.mangorage.swiss.storage.network.Network;

import java.util.Optional;
import java.util.UUID;

public final class StorageNetworkManager {
    private static StorageNetworkManager instance = null;

    public static StorageNetworkManager getInstance() {
        return instance;
    }

    static void start() {
        instance = new StorageNetworkManager();
    }

    static void stop() {
        instance = null;
    }

    private final Int2ObjectArrayMap<Network> networks = new Int2ObjectArrayMap<>();

    /**
     * Handles getting a network
     * Cant use on a client!
     */
    public Network getOrCreateNetwork(MinecraftServer server, UUID owner, int id) {
        if (server == null) throw new IllegalStateException("Cant create network, need MinecraftServer Instance...");
        return networks.computeIfAbsent(id, id2 -> new Network(owner));
    }
}
