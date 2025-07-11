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
import org.mangorage.swiss.screen.interfaces.importer.ImporterMenu;
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

public final class ItemImporterBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, IRightClickable, HasMenu {
    private int ticks = 0;
    public final List<ItemStack> importItems = new ArrayList<>();

    public ItemImporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.IMPORTER_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork();

            IItemHandler input = getInput();
            if (input == null) return;
            final var networkLookup = ItemHandlerLookup.getLookupForInsert(getNetwork());

            if (importItems.isEmpty()) {
                ItemStack remainder;

                for (int slot = 0; slot < input.getSlots(); slot++) {
                    if (input.getStackInSlot(slot).isEmpty()) continue;
                    remainder = input.extractItem(slot, Math.min(input.getSlotLimit(slot), 8), false);
                    remainder = networkLookup.insertIntoHandlers(remainder);
                    if (remainder.isEmpty()) {
                        break;
                    } else {
                        remainder = input.insertItem(slot, remainder, false);
                        if (remainder.isEmpty()) {
                            break;
                        }
                    }
                }
            } else {
                if (input != null && !importItems.isEmpty()) {

                    final var lookup = new ItemHandlerLookup(List.of(input));

                    for (ItemStack stack : importItems) {
                        if (stack == null || stack.isEmpty()) continue;

                        // Try to extract up to 8 items of this type
                        final var result = lookup.findAny(stack.getItem(), 8);
                        if (!result.isEmpty()) {
                            final var remainder = networkLookup.insertIntoHandlers(result);
                            if (!remainder.isEmpty()) {
                                lookup.insertIntoHandlers(remainder);
                            }
                        }
                    }
                }
            }

        }
    }


    IItemHandler getInput() {
        BlockPos outputPos = getBlockPos().relative(getBlockState().getValue(InterfaceNetworkBlock.FACING).getOpposite());
        BlockState outputState = level.getBlockState(outputPos);
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        if (outputState.isAir()) return null;

        return Capabilities.ItemHandler.BLOCK.getCapability(level, outputPos, outputState, outputBE, Direction.DOWN);
    }


    public List<ItemStack> getImportItems() {
        return importItems;
    }

    @Override
    public void onPlayerClick(ItemStack stack, Player player) {

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        tag.put("importItems", ItemStack.OPTIONAL_CODEC.listOf()
                .encodeStart(ops, importItems)
                .getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        if (tag.contains("importItems", Tag.TAG_LIST)) {
            ItemStack.CODEC.listOf()
                    .parse(ops, tag.get("importItems"))
                    .resultOrPartial(error -> {
                        System.err.println("Failed to load importItems: " + error);
                    })
                    .ifPresent(list -> {
                        importItems.clear();
                        importItems.addAll(list);
                    });
        }
    }

    @Override
    public void openMenu(Player player) {
            player.openMenu(new SimpleMenuProvider(
                            (windowId, playerInventory, playerEntity) -> new ImporterMenu(windowId, playerInventory, getBlockPos()),
                            Component.translatable("block.swiss.importer_item_interface")
                    ),
                    buf -> {
                        buf.writeBlockPos(getBlockPos());
                        final var info = StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player);
                        NetworkInfo.LIST_STREAM_CODEC.encode(buf, info);
                    }
            );

    }
}