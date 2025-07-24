package org.mangorage.swiss.data;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.registry.SWISSBlocks;

import java.util.Set;

public class SWISSLootTableProvider extends VanillaBlockLoot {

    public SWISSLootTableProvider(HolderLookup.Provider p_344962_) {
        super(p_344962_);
    }
    @Override
    protected void generate() {

        this.dropSelf(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get());
        this.dropSelf(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get());
        this.dropSelf(SWISSBlocks.IMPORTER_ITEM_INTERFACE_BLOCK.get());
        this.dropSelf(SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK.get());
        this.dropSelf(SWISSBlocks.CRAFTING_ITEM_PANEL_BLOCK.get());

    }


    @Override
    protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
        //Overwrite the core register method to add to our list of known blocks
        super.add(block, table);
        knownBlocks.add(block);
    }
    private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

}
