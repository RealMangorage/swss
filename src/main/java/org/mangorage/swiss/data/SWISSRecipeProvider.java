package org.mangorage.swiss.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public final class SWISSRecipeProvider extends RecipeProvider {

    public SWISSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void buildRecipes(RecipeOutput consumer) {

    }
}