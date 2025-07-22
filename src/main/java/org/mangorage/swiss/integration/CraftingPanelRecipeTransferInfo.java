package org.mangorage.swiss.integration;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.library.transfer.RecipeTransferErrorTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;

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
        return menu.slots.subList(37, 46);
    }

    @Override
    public @NotNull List<Slot> getInventorySlots(CraftingPanelMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        return menu.slots.subList(0, 35);
    }


    @Override
    public @Nullable IRecipeTransferError getHandlingError(CraftingPanelMenu container, RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = recipeHolder.value();
        List<Ingredient> ingredients = recipe.getIngredients();

        if (container.getNetworkHolder() == null || container.getNetworkHolder().getNetwork() == null) {
            return new RecipeTransferErrorTooltip(Component.literal("Network unavailable"));
        }

        ItemHandlerLookup extract = ItemHandlerLookup.getLookupForExtract(container.getNetworkHolder().getNetwork());

        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            boolean foundAny = false;
            for (ItemStack stack : ingredient.getItems()) {
                if (!extract.findAny(stack.getItem(), 1).isEmpty()) {
                    foundAny = true;
                    break;
                }
            }

            if (!foundAny) {
                return new RecipeTransferErrorTooltip(Component.literal("Missing required ingredients in network"));
            }
        }

        return null; // no error, allow transfer
    }



    @Override
    public boolean canHandle(@NotNull CraftingPanelMenu menu, RecipeHolder<CraftingRecipe> recipe) {
        return true;
    }
}
