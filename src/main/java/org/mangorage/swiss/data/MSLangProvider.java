package org.mangorage.swiss.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSBlocks;

public final class MSLangProvider extends LanguageProvider {

    public MSLangProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SWISS.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Creative Tab
        add("itemGroup." + SWISS.MODID, "Mango Storage");

        //Blocks
        addBlock(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK, "Item Exporter Interface");
        addBlock(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK, "Item Storage Interface");
        addBlock(SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK, "Item Storage Panel");

        //GUI
        addGUITranslation("settings_menu", "Open Settings Menu");
        addGUITranslation("settings_menu_title", "Settings");
        addGUITranslation("row_menu", "Click to change rows");
        addGUITranslation("network_menu_title", "Network Settings");
    }


    private void addItemTranslation(String name, String translation) {
        add("item." + SWISS.MODID + "." + name, translation);
    }
    private void addBlockTranslation(String name, String translation) {
        add("block." + SWISS.MODID + "." + name, translation);
    }

    private void addChatTranslation(String name, String translation) {
        add("chat." + SWISS.MODID + "." + name, translation);
    }
    private void addGUITranslation(String name, String translation) {
        add("gui." + SWISS.MODID + "." + name, translation);
    }
    private void addTooltipTranslation(String name, String translation) {
        add("tooltips." + SWISS.MODID + "." + name, translation);
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