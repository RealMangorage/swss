package org.mangorage.swiss.screen.util;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public interface Interact {
    void clicked(ClickType clickType, ItemStack itemStack);
}
