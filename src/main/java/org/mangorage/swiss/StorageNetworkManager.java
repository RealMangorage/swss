package org.mangorage.swiss;

import com.mojang.datafixers.DSL;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.storage.LevelResource;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.network.Permission;
import org.mangorage.swiss.storage.network.UnknownNetwork;
import org.mangorage.swiss.storage.network.User;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public final class StorageNetworkManager extends SavedData {
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
        instance = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(factory(), "swiss-networks");
        instance.setDirty(false);
    }

    static void stop() {
        instance = null;
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
        return networks.computeIfAbsent(id, id2 -> {
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

    void loadInternal(CompoundTag data, HolderLookup.Provider registries) {
        final var networkTags = data.getList("networks", Tag.TAG_COMPOUND);
        for (int id = 0; id < networkTags.size(); id++) {
            CompoundTag networkTag = networkTags.getCompound(id);
            Network network = new Network();
            network.load(networkTag, registries);
            networks.put(networkTag.getInt("id"), network);
        }
    }
}
