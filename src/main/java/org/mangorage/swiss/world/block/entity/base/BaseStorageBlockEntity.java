package org.mangorage.swiss.world.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.StorageNetworkManager;

import java.util.UUID;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice {

    private int networkId = 0;
    private boolean loaded = false;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void connectToNetwork() {
        if (loaded) return;
        loaded = true;
        getNetwork().registerDevice(this);
    }

    public Network getNetwork() {
        return StorageNetworkManager.getInstance().getOrCreateNetwork(level.getServer(), UUID.randomUUID(), getNetworkId());
    }

    @Override
    public void setNetworkId(int id) {
        getNetwork()
                .unregisterDevice(this);
        networkId = id;
        getNetwork()
                .registerDevice(this);
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
