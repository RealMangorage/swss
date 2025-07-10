package org.mangorage.swiss.screen;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface FilterMenu {
    List<ItemStack> getFilterItems();
    void setFilterItems(List<ItemStack> items);
}
