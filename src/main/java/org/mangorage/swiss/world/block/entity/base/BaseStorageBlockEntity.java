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
import org.mangorage.swiss.storage.network.UnknownNetwork;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;

import java.util.UUID;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice, INetworkHolder {

    private UUID networkId = StorageNetworkManager.DEFAULT_NETWORK_ID;
    private UUID owner = null;
    private boolean loaded = false;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag deviceDataTag = new CompoundTag();
        deviceDataTag.putUUID("networkId", networkId);
        if (owner != null)
            deviceDataTag.putUUID("owner", owner);
        tag.put("deviceData", deviceDataTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final var deviceDataTag = tag.getCompound("deviceData");
        if (deviceDataTag.contains("owner"))
            setOwner(deviceDataTag.getUUID("owner"));
        this.networkId = deviceDataTag.getUUID("networkId");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel().isClientSide()) {

        } else {
            setNetworkId(networkId);
        }
    }

    // CUSTOM NETWORK STUFF

    @Override
    public boolean shouldCache() {
        return false;
    }

    protected void connectToNetwork() {
        if (owner == null || loaded) return; // Dont load if Owner hasnt been set yet!
        loaded = true;
        getNetwork().registerDevice(this);

        // TODO: FIX so it properly reflects connection state
        if (getBlockState().hasProperty(InterfaceNetworkBlock.CONNECTED)) {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(InterfaceNetworkBlock.CONNECTED, true), Block.UPDATE_ALL);
        }
    }

    public Network getNetwork() {
        return StorageNetworkManager.getInstance().getNetwork(level.getServer(), getNetworkId());
    }

    @Override
    public void setNetworkId(UUID id) {
        if (this.networkId != id) {
            getNetwork()
                    .unregisterDevice(this);
        }
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
    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public boolean isValidDevice() {
        return !isRemoved();
    }
}
