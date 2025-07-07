package org.mangorage.swiss;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.network.UnknownNetwork;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public final class StorageNetworkManager extends SavedData {
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

    static void save(MinecraftServer server) {
        getInstance().setDirty(true);
        getInstance().save(
                new File("network-data.json"),
                server.registryAccess()
        );
    }

    private final Int2ObjectArrayMap<Network> networks = new Int2ObjectArrayMap<>();

    /**
     * Handles getting a network
     * Cant use on a client!
     */
    public Network getOrCreateNetwork(MinecraftServer server, UUID owner, int id) {
        if (server == null) throw new IllegalStateException("Cant create network, need MinecraftServer Instance...");
        if (owner == null) return UnknownNetwork.INSTANCE;
        return networks.computeIfAbsent(id, id2 -> new Network(owner));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        final var data = new CompoundTag();
        networks.forEach((id, network) -> {
            data.put("network-" + id, network.save(new CompoundTag(), registries));
        });
        return data;
    }
}
