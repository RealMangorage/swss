package org.mangorage.swiss.integration;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.transfer.RecipeTransferErrorInternal;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.network.SyncRecipePacketC2S;
import org.mangorage.swiss.network.request.RequestNetworkItemsPacketC2S;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;

import java.util.List;
import java.util.Optional;

public class CraftingPanelRecipeTransferHandler implements IRecipeTransferHandler<CraftingPanelMenu, RecipeHolder<CraftingRecipe>> {

    @Override
    public Class<? extends CraftingPanelMenu> getContainerClass() {
        return CraftingPanelMenu.class;
    }

    @Override
    public Optional<MenuType<CraftingPanelMenu>> getMenuType() {
        return MSMenuTypes.CRAFTING_MENU.asOptional();
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }


    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            CraftingPanelMenu menu,
            RecipeHolder<CraftingRecipe> recipeHolder,
            IRecipeSlotsView recipeSlotsView,
            Player player,
            boolean maxTransfer,
            boolean doTransfer
    ) {
        if (!doTransfer) {
            return null;
        }

        PacketDistributor.sendToServer(new SyncRecipePacketC2S(recipeHolder.id(), maxTransfer));

        return null;
    }


}
