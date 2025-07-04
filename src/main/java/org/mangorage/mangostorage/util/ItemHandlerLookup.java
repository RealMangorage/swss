package org.mangorage.mangostorage.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class ItemHandlerLookup {
    private final List<IItemHandler> handlers;

    public ItemHandlerLookup(List<IItemHandler> handlers) {
        this.handlers = handlers;
    }

    public Optional<LookupResult> findAny(Item item, int amount) {
        // If Item Max size is less then amount
        amount = Math.min(amount, item.getDefaultMaxStackSize());

        ItemStack lookForItem = item.getDefaultInstance();
        for (IItemHandler handler : handlers) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (handler.isItemValid(slot, lookForItem)) {
                    ItemStack simulated = handler.extractItem(slot, amount, true);
                    if (simulated.getItem() == item) {
                        return Optional.of(
                                new LookupResult(
                                        handler,
                                        slot,
                                        handler.extractItem(slot, amount, false)
                                )
                        );
                    }
                }
            }
        }

        return Optional.empty();
    }

    public record LookupResult(IItemHandler handler, int slot, ItemStack stack) {
        public void insert(IItemHandler other) {
            ItemStack remainder = stack;
            for (int slot = 0; slot < other.getSlots(); slot++) {
                remainder = other.insertItem(slot, remainder, false);
            }

            if (!remainder.isEmpty()) {
                handler.insertItem(slot, remainder, false);
            }
        }
    }
}
