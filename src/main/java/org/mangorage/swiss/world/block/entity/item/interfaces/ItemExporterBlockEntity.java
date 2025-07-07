package org.mangorage.swiss.world.block.entity.item.interfaces;

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
import org.mangorage.swiss.storage.util.IRightClickable;
import org.mangorage.swiss.registry.SWISSBlockEntities;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.List;

public final class ItemExporterBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, IRightClickable {
    private int ticks = 0;
    private Item exportItem = Items.AIR;

    public ItemExporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.EXPORTER_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork();

            IItemHandler output = getOutput();

            if (output != null && ItemHandlerLookup.hasRoom(output, exportItem.getDefaultInstance())) {

                final var lookup = ItemHandlerLookup.getLookupForExtract(getNetwork());
                final var result = lookup.findAny(exportItem, 8);

                if (!result.isEmpty()) {
                    final var remainder = ItemHandlerLookup.insertIntoHandlers(List.of(output), result);
                    if (!remainder.isEmpty()) {
                        lookup.insertIntoHandlers(remainder);
                    }
                }
            }

        }
    }

    IItemHandler getOutput() {
        BlockPos outputPos = getBlockPos().relative(getBlockState().getValue(InterfaceNetworkBlock.FACING).getOpposite());
        BlockState outputState = level.getBlockState(outputPos);
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        if (outputState.isAir()) return null;

        return Capabilities.ItemHandler.BLOCK.getCapability(level, outputPos, outputState, outputBE, Direction.DOWN);
    }

    @Override
    public void onPlayerClick(ItemStack stack, Player player) {
        exportItem = stack.getItem();
    }
}
