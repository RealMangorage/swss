package org.mangorage.swiss.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSBlocks;

import java.util.concurrent.CompletableFuture;

public final class SWISSBlockTags extends BlockTagsProvider {

    SWISSBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SWISS.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        // Pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get())
                .add(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get())
                .add(SWISSBlocks.IMPORTER_ITEM_INTERFACE_BLOCK.get())
                .add(SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK.get())
                ;
        // Add block tags here if needed
    }
}
