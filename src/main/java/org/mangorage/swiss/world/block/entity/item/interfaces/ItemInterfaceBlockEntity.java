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
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
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
        BlockPos outputPos = getBlockPos().relative(getBlockState().getValue(InterfaceNetworkBlock.FACING).getOpposite());
        BlockState outputState = level.getBlockState(outputPos);
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        if (outputState.isAir()) return null;

        return Capabilities.ItemHandler.BLOCK.getCapability(level, outputPos, outputState, outputBE, Direction.DOWN);
    }

    @Override
    public boolean canInsert(DeviceType type) {
        return true;
    }

    @Override
    public boolean canExtract(DeviceType type) {
        return true;
    }
}
