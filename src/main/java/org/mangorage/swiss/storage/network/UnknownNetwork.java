package org.mangorage.swiss.storage.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public final class UnknownNetwork extends Network {
    public static final UnknownNetwork INSTANCE = new UnknownNetwork(UUID.randomUUID());

    UnknownNetwork(UUID owner) {
        super(owner);
    }

    @Override
    public boolean hasPermission(UUID userId, Set<Permission> permissions) {
        return false;
    }

    @Override
    public void registerUser(User user) {}

    @Override
    public void unregisterUser(User user) {}

    @Override
    public void registerDevice(IDevice device) {}

    @Override
    public void unregisterDevice(IDevice device) {}

    @Override
    public Stream<ItemDevice> getItemDevices() {
        return Stream.empty();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registries) {
        return null;
    }
}
