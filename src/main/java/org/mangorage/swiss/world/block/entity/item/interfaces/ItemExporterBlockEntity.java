package org.mangorage.swiss.world.block.entity.item.interfaces;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.screen.interfaces.exporter.ExporterMenu;
import org.mangorage.swiss.screen.util.HasMenu;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.storage.util.IRightClickable;
import org.mangorage.swiss.registry.SWISSBlockEntities;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.ArrayList;
import java.util.List;

public final class ItemExporterBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, IRightClickable, HasMenu {
    private int ticks = 0;
    public final List<ItemStack> exportItems = new ArrayList<>();

    public ItemExporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.EXPORTER_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {

            connectToNetwork();

            IItemHandler output = getOutput();

            if (output != null) {
                for (ItemStack exportStack : exportItems) {
                    if (ItemHandlerLookup.hasRoom(output, exportStack)) {

                        final var lookup = ItemHandlerLookup.getLookupForExtract(getNetwork());
                        final var result = lookup.findAny(exportStack.getItem(), Math.min(exportStack.getMaxStackSize(), 8));

                        if (!result.isEmpty()) {
                            final var remainder = ItemHandlerLookup.insertIntoHandlers(List.of(output), result);
                            if (!remainder.isEmpty()) {
                                lookup.insertIntoHandlers(remainder);
                            }
                        }
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

    public List<ItemStack> getExportItems() {
        return exportItems;
    }

    @Override
    public void onPlayerClick(ItemStack stack, Player player) {
        //exportItem = stack.getItem();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        tag.put("exportItems", ItemStack.OPTIONAL_CODEC.listOf()
                .encodeStart(ops, exportItems)
                .getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

        if (tag.contains("exportItems", Tag.TAG_LIST)) {
            ItemStack.CODEC.listOf()
                    .parse(ops, tag.get("exportItems"))
                    .resultOrPartial(error -> {
                        System.err.println("Failed to load exportItems: " + error);
                    })
                    .ifPresent(list -> {
                        exportItems.clear();
                        exportItems.addAll(list);
                    });
        }
    }

    @Override
    public void openMenu(Player player) {
        player.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new ExporterMenu(windowId, playerInventory, getBlockPos()),
                        Component.translatable("block.swiss.exporter_item_interface")
                ),
                buf -> {
                    buf.writeBlockPos(getBlockPos());
                    final var info = StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player);
                    NetworkInfo.LIST_STREAM_CODEC.encode(buf, info);
                }
        );

    }
}
