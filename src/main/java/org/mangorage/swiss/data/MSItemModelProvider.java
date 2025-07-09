package org.mangorage.swiss.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSItems;

public final class MSItemModelProvider extends ItemModelProvider {

    public MSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SWISS.MODID, existingFileHelper);
    }


    @Override
    protected void registerModels() {

        //Add item models

        basicItem(SWISSItems.SWISS_CHEESE.get());
    }
}
