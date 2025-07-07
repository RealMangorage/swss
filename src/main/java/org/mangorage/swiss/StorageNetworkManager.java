package org.mangorage.swiss;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.storage.LevelResource;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.network.UnknownNetwork;
import java.nio.file.Path;
import java.util.UUID;

public final class StorageNetworkManager extends SavedData {
    private static StorageNetworkManager instance = null;

    static SavedData.Factory<StorageNetworkManager> factory() {
        return new SavedData.Factory<>(StorageNetworkManager::new, StorageNetworkManager::load);
    }

    public static StorageNetworkManager load(CompoundTag tag, HolderLookup.Provider registries) {
        return new StorageNetworkManager();
    }

    public static StorageNetworkManager getInstance() {
        return instance;
    }

    static void start(MinecraftServer server) {

        instance = factory().deserializer().apply(new CompoundTag(), server.registryAccess());
        instance.setDirty(false);
    }

    static void stop() {
        instance = null;
    }

    static void save(MinecraftServer server) {
        try {
            getInstance().checkDirty();
            getInstance().save(
                    server.getWorldPath(LevelResource.ROOT).resolve("data/swiss-network-data.dat").toFile(),
                    server.registryAccess()
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private final Int2ObjectArrayMap<Network> networks = new Int2ObjectArrayMap<>();

    StorageNetworkManager() {}

    /**
     * Handles getting a network
     * Cant use on a client!
     */
    public Network getOrCreateNetwork(MinecraftServer server, UUID owner, int id) {
        if (server == null) throw new IllegalStateException("Cant create network, need MinecraftServer Instance...");
        if (owner == null) return UnknownNetwork.INSTANCE;
        return networks.computeIfAbsent(id, id2 -> new Network(owner));
    }

    void checkDirty() {
        for (Network network : networks.values()) {
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

        networks.forEach((id, network) -> {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putInt("id", id);
            networksTag.add(network.save(networkTag, registries));
        });

        data.put("networks", networksTag);

        return data;
    }
}
