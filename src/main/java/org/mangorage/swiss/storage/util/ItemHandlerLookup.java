package org.mangorage.swiss.storage.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.swiss.storage.device.DeviceType;
import org.mangorage.swiss.storage.device.ItemDevice;
import org.mangorage.swiss.storage.network.Network;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ItemHandlerLookup {

    public static boolean hasRoom(IItemHandler handler, ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (!handler.isItemValid(slot, stack)) continue;

            ItemStack inSlot = handler.getStackInSlot(slot);

            // If slot is empty, we're golden
            if (inSlot.isEmpty()) return true;

            // If stackable and room to grow
            if (ItemStack.isSameItemSameComponents(inSlot, stack)) {
                int max = Math.min(inSlot.getMaxStackSize(), handler.getSlotLimit(slot));
                if (inSlot.getCount() < max) return true;
            }
        }

        return false;
    }

    public static ItemStack insertIntoHandlers(List<IItemHandler> handlers, ItemStack inputStack) {
        if (inputStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = inputStack.copy();
        boolean canInsertAnywhere = false;

        // First pass: check if **any** slot can accept even 1 item
        outer:
        for (IItemHandler handler : handlers) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (!handler.isItemValid(slot, remaining)) continue;

                ItemStack simulated = handler.insertItem(slot, remaining, true);
                if (!simulated.equals(remaining)) {
                    canInsertAnywhere = true;
                    break outer;
                }
            }
        }

        if (!canInsertAnywhere) return inputStack;

        // Second pass: actually insert the little bastard
        for (IItemHandler handler : handlers) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (!handler.isItemValid(slot, remaining)) continue;

                remaining = handler.insertItem(slot, remaining, false);
                if (remaining.isEmpty()) return ItemStack.EMPTY;
            }
        }

        return remaining;
    }


    private final List<IItemHandler> handlers;

    public ItemHandlerLookup(List<IItemHandler> handlers) {
        this.handlers = handlers;
    }

    public static List<IItemHandler> getItemHandlersForInsertNetwork(Network network) {
        return network
                .getItemDevices()
                .filter(itemDevice -> itemDevice.isValidDevice() && itemDevice.canExtract(DeviceType.ITEM))
                .map(ItemDevice::getItemHandler)
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<IItemHandler> getItemHandlersForExtractNetwork(Network network) {
        return network
                .getItemDevices()
                .filter(itemDevice -> itemDevice.isValidDevice() && itemDevice.canExtract(DeviceType.ITEM))
                .map(ItemDevice::getItemHandler)
                .filter(Objects::nonNull)
                .toList();
    }

    public static ItemHandlerLookup getLookupForExtract(Network network) {
        return new ItemHandlerLookup(getItemHandlersForExtractNetwork(network));
    }

    public static ItemHandlerLookup getLookupForInsert(Network network) {
        return new ItemHandlerLookup(getItemHandlersForInsertNetwork(network));
    }

    public ItemStack findAny(Item item, int amount) {
        amount = Math.min(amount, item.getDefaultMaxStackSize());

        ItemStack collected = ItemStack.EMPTY;
        int remaining = amount;

        for (IItemHandler handler : handlers) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
//                if (!handler.isItemValid(slot, lookForItem)) continue;

                ItemStack simulated = handler.extractItem(slot, remaining, true);
                if (!simulated.isEmpty() && simulated.getItem() == item) {
                    int extractCount = Math.min(simulated.getCount(), remaining);
                    ItemStack extracted = handler.extractItem(slot, extractCount, false);

                    if (!extracted.isEmpty()) {
                        if (collected.isEmpty()) {
                            collected = extracted.copy();
                        } else {
                            collected.grow(extracted.getCount());
                        }
                        remaining -= extracted.getCount();
                    }

                    if (remaining <= 0) break;
                }
            }
            if (remaining <= 0) break;
        }

        return collected;
    }

    public ItemStack insertIntoHandlers(ItemStack stack) {
        return insertIntoHandlers(handlers, stack);
    }
}
