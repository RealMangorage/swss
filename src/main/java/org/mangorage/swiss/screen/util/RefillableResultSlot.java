package org.mangorage.swiss.screen.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.mangorage.swiss.network.SyncRecipePacketC2S;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;

import java.util.ArrayList;
import java.util.List;

public class RefillableResultSlot extends ResultSlot {

    private final CraftingPanelMenu menu;

    public RefillableResultSlot(Player player, CraftingContainer craftMatrix, Container craftResult, int index, int x, int y, CraftingPanelMenu menu) {
        super(player, craftMatrix, craftResult, index, x, y);
        this.menu = menu;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);

    }
}