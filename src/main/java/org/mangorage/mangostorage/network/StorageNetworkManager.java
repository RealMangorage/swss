package org.mangorage.mangostorage.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public final class StorageNetworkManager {
    private static StorageNetworkManager instance = null;

    public static Optional<StorageNetworkManager> getInstance() {
        if (instance == null) return Optional.empty();
        return Optional.of(instance);
    }

    public static void start() {
        instance = new StorageNetworkManager();
    }

    public static void stop() {
        instance = null;
    }

    private final Int2ObjectArrayMap<INetwork> networks = new Int2ObjectArrayMap<>();

    /**
     * Handles getting a network
     * Cant use on a client!
     */
    public INetwork getOrCreateNetwork(MinecraftServer server, int id) {
        if (server == null) throw new IllegalStateException("Cant create network, need MinecraftServer Instance...");
        return networks.computeIfAbsent(id, id2 -> new NetworkImpl());
    }
}
