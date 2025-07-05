package org.mangorage.mangostorage.world.block.entity.item.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.mangostorage.storage.network.INetwork;
import org.mangorage.mangostorage.storage.util.IRightClickable;
import org.mangorage.mangostorage.storage.device.ItemDevice;
import org.mangorage.mangostorage.registry.MSBlockEntities;
import org.mangorage.mangostorage.util.ItemHandlerLookup;
import org.mangorage.mangostorage.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.mangostorage.world.block.entity.TickingBlockEntity;

import java.util.Objects;

public final class ItemExporterBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, IRightClickable {
    private int ticks = 0;
    private Item exportItem = Items.AIR;

    public ItemExporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(MSBlockEntities.EXPORTER_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork();

            IItemHandler output = getOutput();

            if (output != null) {

                INetwork network = getNetwork();
                ItemHandlerLookup lookup = new ItemHandlerLookup(
                        network
                                .getItemDevices()
                                .filter(itemDevice -> itemDevice.isValidDevice() && itemDevice.canExtract())
                                .map(ItemDevice::getItemHandler)
                                .filter(Objects::nonNull)
                                .toList()
                );

                lookup.findAny(exportItem, 32).ifPresent(result -> {
                    result.insert(output);
                });

            }
        }
    }

    IItemHandler getOutput() {
        BlockPos above = getBlockPos().above();
        BlockState aboveState = level.getBlockState(above);
        BlockEntity aboveEntity = level.getBlockEntity(above);
        return Capabilities.ItemHandler.BLOCK.getCapability(level, above, aboveState, aboveEntity, Direction.DOWN);
    }

    @Override
    public void onPlayerClick(ItemStack stack, Player player) {
        exportItem = stack.getItem();
    }
}
