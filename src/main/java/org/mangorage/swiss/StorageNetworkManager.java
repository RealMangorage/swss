package org.mangorage.swiss;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.network.Permission;
import org.mangorage.swiss.storage.network.UnknownNetwork;
import org.mangorage.swiss.storage.network.User;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StorageNetworkManager extends SavedData {
    public static final UUID DEFAULT_NETWORK_ID = UUID.fromString("e9e13bd4-2b2b-49a3-b522-25ae87ed1c0f");

    private static StorageNetworkManager instance = null;

    static SavedData.Factory<StorageNetworkManager> factory() {
        return new SavedData.Factory<>(StorageNetworkManager::new, StorageNetworkManager::load);
    }

    static StorageNetworkManager load(CompoundTag tag, HolderLookup.Provider registries) {
        final var networkManager = new StorageNetworkManager();
        networkManager.loadInternal(tag, registries);
        return networkManager;
    }

    public static StorageNetworkManager getInstance() {
        return instance;
    }

    static void start(MinecraftServer server) {
        instance = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(factory(), "swiss-network-data");
        instance.setDirty(false);
    }

    static void stop() {
        instance = null;
    }

        private final Map<UUID, Network> networkMap = new HashMap<>();

    StorageNetworkManager() {}

    /**
     * Handles getting a network
     * Cant use on a client!
     */
    public Network getOrCreateNetwork(MinecraftServer server, UUID owner, UUID id) {
        if (server == null) throw new IllegalStateException("Cant create network, need MinecraftServer Instance...");
        if (owner == null) return UnknownNetwork.INSTANCE;
        return networkMap.computeIfAbsent(id, id2 -> {
            final var network = new Network();
            final var user = new User(owner);
            user.addPermission(Permission.OWNER);
            network.registerUser(user);
            return network;
        });
    }

    @Override
    public void save(File file, HolderLookup.Provider registries) {
        checkDirty();
        super.save(file, registries);
    }

    void checkDirty() {
        for (Network network : networkMap.values()) {
            if (network.isDirty()) {
                setDirty(true);
                break;
            }
        }
    }


    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        final var data = new CompoundTag();

        final var networksTag = new ListTag();

        networkMap.forEach((id, network) -> {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putUUID("id", id);
            networksTag.add(network.save(networkTag, registries));
        });

        data.put("networks", networksTag);

        return data;
    }

    void loadInternal(CompoundTag data, HolderLookup.Provider registries) {
        final var networkTags = data.getList("networks", Tag.TAG_COMPOUND);
        for (int id = 0; id < networkTags.size(); id++) {
            CompoundTag networkTag = networkTags.getCompound(id);
            Network network = new Network();
            network.load(networkTag, registries);
            networkMap.put(networkTag.getUUID("id"), network);
        }
    }
}
