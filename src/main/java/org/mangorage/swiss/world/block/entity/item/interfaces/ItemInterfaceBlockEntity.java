package org.mangorage.swiss.world.block.entity.item.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.swiss.storage.device.DeviceType;
import org.mangorage.swiss.storage.device.ItemDevice;
import org.mangorage.swiss.registry.SWISSBlockEntities;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

public final class ItemInterfaceBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, ItemDevice {

    private int ticks = 0;

    public ItemInterfaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.STORAGE_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
    }


    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork(); // Connect if we havent already done so...
        }
    }

    @Override
    public IItemHandler getItemHandler() {
        BlockPos above = getBlockPos().above();
        BlockState aboveState = level.getBlockState(above);
        BlockEntity aboveEntity = level.getBlockEntity(above);
        return Capabilities.ItemHandler.BLOCK.getCapability(level, above, aboveState, aboveEntity, Direction.DOWN);
    }

    @Override
    public boolean canInsert(DeviceType type) {
        return false;
    }

    @Override
    public boolean canExtract(DeviceType type) {
        return true;
    }
}
