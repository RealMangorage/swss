package org.mangorage.swiss.world.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.storage.network.UnknownNetwork;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;

import java.util.UUID;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice {

    private int networkId = 0;
    private UUID owner = null;
    private boolean loaded = false;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void connectToNetwork() {
        if (owner == null || loaded) return; // Dont load if Owner hasnt been set yet!
        loaded = true;
        getNetwork().registerDevice(this);
        if (getBlockState().hasProperty(InterfaceNetworkBlock.CONNECTED)) {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(InterfaceNetworkBlock.CONNECTED, true), Block.UPDATE_ALL);
        }
    }

    public Network getNetwork() {
        if (getOwner() == null) return UnknownNetwork.INSTANCE;
        return StorageNetworkManager.getInstance().getOrCreateNetwork(level.getServer(), getOwner(), getNetworkId());
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
    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    @Override
    public UUID getOwner() {
        return owner;
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
