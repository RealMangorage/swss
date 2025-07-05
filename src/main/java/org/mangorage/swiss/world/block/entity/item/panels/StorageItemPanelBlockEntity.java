package org.mangorage.swiss.world.block.entity.item.panels;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.swiss.storage.device.DeviceType;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.device.ItemDevice;
import org.mangorage.swiss.registry.MSBlockEntities;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StorageItemPanelBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, ItemDevice {

    private int ticks = 0;
    public final ContainerData data;

    public StorageItemPanelBlockEntity(BlockPos pos, BlockState blockState) {
        super(MSBlockEntities.STORAGE_ITEM_PANEL_BLOCK_ENTITY.get(), pos, blockState);
        //Whatever data we need to store in the container
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }



    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork(); // Connect if we havent already done so...
        }
    }

    public List<ItemStack> getItems() {
        return getNetwork()
                .getItemDevices()
                .filter(device -> device.isValidDevice() && device.canExtract(DeviceType.ITEM))
                .map(ItemDevice::getItemHandler)
                .filter(Objects::nonNull)
                .map(handler -> {
                    List<ItemStack> stacks = new ArrayList<>();

                    for (int slot = 0; slot < handler.getSlots(); slot++) {
                        stacks.add(handler.getStackInSlot(slot).copy());
                    }

                    return stacks;
                })
                .flatMap(List::stream)
                .filter(item -> !item.isEmpty())
                .toList();
    }

    @Override
    public IItemHandler getItemHandler() {
        return null;
    }

    @Override
    public boolean canInsert(DeviceType type) {
        return false;
    }

    @Override
    public boolean canExtract(DeviceType type) {
        return false;
    }
}
