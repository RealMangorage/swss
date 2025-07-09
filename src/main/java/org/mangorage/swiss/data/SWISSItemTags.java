package org.mangorage.swiss.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSItems;

import java.util.concurrent.CompletableFuture;

public final class SWISSItemTags extends ItemTagsProvider {

    SWISSItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags.contentsGetter(), SWISS.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Item Tags
        tag(Tags.Items.FOODS)
                .add(SWISSItems.SWISS_CHEESE.get())
                ;
    }
}