package org.mangorage.mangostorage.storage.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IRightClickable {
    void onPlayerClick(ItemStack stack, Player player);
}
