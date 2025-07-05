package org.mangorage.swiss.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.MSBlocks;

public final class MSBlockStatesProvider extends BlockStateProvider {

    public MSBlockStatesProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SWISS.MODID, existingFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get(), models()
                .cubeAll(blockTexture(MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get()).getPath(), blockTexture(MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get())));

        simpleBlockWithItem(MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get(), models()
                .cubeAll(blockTexture(MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get()).getPath(), blockTexture(MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get())));
    }


    @Override
    public String getName() {
        return SWISS.MODID + " Block States";
    }
}
