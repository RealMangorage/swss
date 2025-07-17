package org.mangorage.swiss.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.common.Internal;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.SWISS;

@JeiPlugin
public class JEISWISSPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SWISS.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

        registration.addRecipeTransferHandler(new CraftingPanelRecipeTransferInfo());

        //registration.addRecipeTransferHandler(new ItemTransferHandler(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new CraftingPanelRecipeTransferInfo()), RecipeTypes.CRAFTING);

        //registration.addRecipeTransferHandler(new CraftingPanelRecipeTransferInfo(Internal.getServerConnection(), registration.getJeiHelpers().getStackHelper(), registration.getTransferHelper(), new CraftingPanelRecipeTransferInfo()), RecipeTypes.CRAFTING);
        //registration.addRecipeTransferHandler(new CraftingPanelRecipeTransferInfo());
    }
}
