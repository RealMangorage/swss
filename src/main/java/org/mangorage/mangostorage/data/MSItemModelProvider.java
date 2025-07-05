package org.mangorage.mangostorage.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.registry.MSBlocks;

public final class MSItemModelProvider extends ItemModelProvider {

    public MSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MangoStorage.MODID, existingFileHelper);


    }


    @Override
    protected void registerModels() {

        //Add item models
    }
}
