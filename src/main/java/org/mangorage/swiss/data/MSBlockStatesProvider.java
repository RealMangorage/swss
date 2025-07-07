package org.mangorage.swiss.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSBlocks;

public final class MSBlockStatesProvider extends BlockStateProvider {

    public MSBlockStatesProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SWISS.MODID, existingFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {
    /*
        simpleBlockWithItem(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get(), models()
                .cubeAll(blockTexture(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get()).getPath(), blockTexture(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get())));

        simpleBlockWithItem(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get(), models()
                .cubeAll(blockTexture(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get()).getPath(), blockTexture(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get())));
    */

    }



    @Override
    public String getName() {
        return SWISS.MODID + " Block States";
    }
}
