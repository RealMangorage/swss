package org.mangorage.mangostorage.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.registry.MSBlocks;

public class MSBlockStatesProvider extends BlockStateProvider {

    public MSBlockStatesProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MangoStorage.MODID, existingFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(MSBlocks.EXPORTER_BLOCK.get(), models()
                .cubeAll(blockTexture(MSBlocks.EXPORTER_BLOCK.get()).getPath(), blockTexture(MSBlocks.EXPORTER_BLOCK.get())));

        simpleBlockWithItem(MSBlocks.INTERFACE_BLOCK.get(), models()
                .cubeAll(blockTexture(MSBlocks.INTERFACE_BLOCK.get()).getPath(), blockTexture(MSBlocks.INTERFACE_BLOCK.get())));
    }


    @Override
    public String getName() {
        return MangoStorage.MODID + " Block States";
    }
}
