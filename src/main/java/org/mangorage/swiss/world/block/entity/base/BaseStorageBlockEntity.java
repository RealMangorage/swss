package org.mangorage.swiss.world.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.network.INetwork;
import org.mangorage.swiss.storage.StorageNetworkManager;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice {

    private int networkId = 0;
    private boolean loaded = false;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }


    protected void connectToNetwork() {
        if (loaded) return;
        loaded = true;
        StorageNetworkManager.getInstance().ifPresent(manager -> {
            manager.getOrCreateNetwork(level.getServer(), getNetworkId())
                    .registerDevice(this);
        });
    }

    protected INetwork getNetwork() {
        return StorageNetworkManager.getInstance().get().getOrCreateNetwork(level.getServer(), getNetworkId());
    }

    @Override
    public void setNetworkId(int id) {
        StorageNetworkManager.getInstance().ifPresent(manager -> {
            manager.getOrCreateNetwork(level.getServer(), getNetworkId())
                    .unregisterDevice(this);
        });
        networkId = id;
        StorageNetworkManager.getInstance().ifPresent(manager -> {
            manager.getOrCreateNetwork(level.getServer(), getNetworkId())
                    .registerDevice(this);
        });
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public boolean isValidDevice() {
        return !isRemoved();
    }
}
