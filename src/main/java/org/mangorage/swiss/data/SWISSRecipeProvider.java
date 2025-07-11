package org.mangorage.swiss.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.mangorage.swiss.registry.SWISSItems;

import java.util.concurrent.CompletableFuture;

public final class SWISSRecipeProvider extends RecipeProvider {

    public SWISSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void buildRecipes(RecipeOutput consumer) {

        //Item Panel
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWISSItems.STORAGE_ITEM_PANEL_ITEM.get(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('C', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND)).save(consumer);

        //Item Storage Interface
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWISSItems.STORAGE_ITEM_INTERFACE_ITEM.get(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.DUSTS_REDSTONE)
                .define('C', Tags.Items.CHESTS_WOODEN)
                .unlockedBy("has_item", has(Tags.Items.DUSTS_REDSTONE)).save(consumer);

        //Item Exporter Interface
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWISSItems.EXPORTER_ITEM_INTERFACE_ITEM.get(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.DUSTS_REDSTONE)
                .define('C', Items.HOPPER)
                .unlockedBy("has_item", has(Tags.Items.DUSTS_REDSTONE)).save(consumer);

        //Item Importer Interface
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWISSItems.IMPORTER_ITEM_INTERFACE_ITEM.get(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.GEMS_LAPIS)
                .define('C', Items.HOPPER)
                .unlockedBy("has_item", has(Tags.Items.DUSTS_REDSTONE)).save(consumer);

    }
}