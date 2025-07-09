package org.mangorage.swiss.world.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.mangorage.swiss.storage.device.INetworkHolder;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;

import java.util.UUID;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice, INetworkHolder {

    private UUID networkId = null;
    private UUID owner = null;
    private Network network = null;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag deviceDataTag = new CompoundTag();
        if (owner != null) {
            deviceDataTag.putUUID("owner", owner);
            if (networkId != null)
                deviceDataTag.putUUID("networkId", networkId);
        }
        tag.put("deviceData", deviceDataTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final var deviceDataTag = tag.getCompound("deviceData");
        if (deviceDataTag.contains("owner"))
            setOwner(deviceDataTag.getUUID("owner"));
        this.networkId = deviceDataTag.contains("networkId") ? deviceDataTag.getUUID("networkId") : null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel().isClientSide()) {

        } else {
            setNetwork(networkId);
        }
    }

    // CUSTOM NETWORK STUFF

    @Override
    public boolean shouldCache() {
        return true;
    }

    protected void connectToNetwork() {
        if (owner == null || network != null) return;
        this.network = StorageNetworkManager.getInstance().getNetwork(level.getServer(), getNetworkId());

        getNetwork().registerDevice(this);

        // TODO: FIX so it properly reflects connection state
        if (getBlockState().hasProperty(InterfaceNetworkBlock.CONNECTED)) {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(InterfaceNetworkBlock.CONNECTED, true), Block.UPDATE_ALL);
        }
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(UUID id) {
        if (this.networkId != id) {
            getNetwork()
                    .unregisterDevice(this);
        }

        network = null;

        networkId = id;

        connectToNetwork();

        if (network != null) {
            getNetwork()
                    .registerDevice(this);
        }
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
    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public boolean isValidDevice() {
        return !isRemoved();
    }
}
