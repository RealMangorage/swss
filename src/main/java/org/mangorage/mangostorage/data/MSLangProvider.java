package org.mangorage.mangostorage.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.registry.MSBlocks;

public final class MSLangProvider extends LanguageProvider {

    public MSLangProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MangoStorage.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Creative Tab
        add("itemGroup." + MangoStorage.MODID, "Mango Storage");

        //Blocks
        addBlock(MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK, "Item Exporter Interface");
        addBlock(MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK, "Item Storage Interface");
        addBlock(MSBlocks.STORAGE_ITEM_PANEL_BLOCK, "Item Storage Panel");
    }


    private void addItemTranslation(String name, String translation) {
        add("item." + MangoStorage.MODID + "." + name, translation);
    }
    private void addBlockTranslation(String name, String translation) {
        add("block." + MangoStorage.MODID + "." + name, translation);
    }

    private void addChatTranslation(String name, String translation) {
        add("chat." + MangoStorage.MODID + "." + name, translation);
    }
    private void addGUITranslation(String name, String translation) {
        add("gui." + MangoStorage.MODID + "." + name, translation);
    }
    private void addTooltipTranslation(String name, String translation) {
        add("tooltips." + MangoStorage.MODID + "." + name, translation);
    }

    public static String capitalizeFirstLetterOfEachWord(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase()) // Capitalize first letter
                        .append(word.substring(1).toLowerCase()) // Keep the rest lowercase
                        .append(" ");
            }
        }
        return result.toString().trim(); // Remove trailing space
    }

}