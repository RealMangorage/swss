package org.mangorage.swiss.integration;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemTransferHandler extends BasicRecipeTransferHandler<CraftingPanelMenu, CraftingRecipe> {
    public ItemTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<CraftingPanelMenu, CraftingRecipe> transferInfo) {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(CraftingPanelMenu menu, CraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        return super.transferRecipe(menu, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}