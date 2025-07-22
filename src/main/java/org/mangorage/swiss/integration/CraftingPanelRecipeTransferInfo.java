package org.mangorage.swiss.integration;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftingPanelRecipeTransferInfo implements IRecipeTransferInfo<CraftingPanelMenu, RecipeHolder<CraftingRecipe>> {

    @Override
    public @NotNull Class<? extends CraftingPanelMenu> getContainerClass() {
        return CraftingPanelMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<CraftingPanelMenu>> getMenuType() {
        return Optional.of(MSMenuTypes.CRAFTING_MENU.get());
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public @NotNull List<Slot> getRecipeSlots(CraftingPanelMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        return menu.getCraftingSlots(); // crafting grid
    }

    @Override
    public @NotNull List<Slot> getInventorySlots(CraftingPanelMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        List<Slot> inventorySlots = new ArrayList<>();
        for (int i = 10; i < 46; i++) {
            if (i == 36) continue; // Exclude invalid slot
            inventorySlots.add(menu.getSlot(i));
        }
        return inventorySlots;
    }

    @Override
    public boolean canHandle(@NotNull CraftingPanelMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        return true;
    }
}
