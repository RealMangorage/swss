package org.mangorage.swiss.world.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.storage.device.INetworkHolder;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.storage.network.UnknownNetwork;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;

import java.util.UUID;

public abstract class BaseStorageBlockEntity extends BlockEntity implements IDevice, INetworkHolder {

    private UUID networkId = null;
    private UUID owner = null;
    private Network network = UnknownNetwork.INSTANCE;

    public BaseStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        loadAdditional(compoundTag, provider);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        saveAdditional(compoundTag, provider);
        return compoundTag;
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
        if (owner == null || network != UnknownNetwork.INSTANCE) return;
        setNetwork(getNetworkId());
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

        network = UnknownNetwork.INSTANCE;

        networkId = id;

        network = StorageNetworkManager.getInstance().getNetwork(getLevel().getServer(), getNetworkId());

        getNetwork()
                .registerDevice(this);

        if (getBlockState().hasProperty(InterfaceNetworkBlock.CONNECTED)) {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(InterfaceNetworkBlock.CONNECTED, getNetwork() != UnknownNetwork.INSTANCE), Block.UPDATE_ALL);
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
