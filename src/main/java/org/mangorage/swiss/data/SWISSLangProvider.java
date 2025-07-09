package org.mangorage.swiss.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.registry.SWISSBlocks;
import org.mangorage.swiss.registry.SWISSItems;

public final class SWISSLangProvider extends LanguageProvider {

    public SWISSLangProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SWISS.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Creative Tab
        add("itemGroup." + SWISS.MODID, "SWISS");

        //Blocks
        addBlock(SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK, "Item Exporter Interface");
        addBlock(SWISSBlocks.IMPORTER_ITEM_INTERFACE_BLOCK, "Item Importer Interface");
        addBlock(SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK, "Item Storage Interface");
        addBlock(SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK, "Item Storage Panel");

        addItem(SWISSItems.SWISS_CHEESE, "Swiss Cheese");

        //GUI
        addGUITranslation("settings_menu", "Open Settings Menu");
        addGUITranslation("settings_menu_title", "Settings");
        addGUITranslation("network_settings", "Open Network Settings");
        addGUITranslation("create_join_network_settings", "Open Create/Join Network Settings");
        addGUITranslation("row_menu", "Click to change rows");
        addGUITranslation("network_menu_title", "Network Settings");
        addGUITranslation("manger_menu_title", "Manage Network");
        addGUITranslation("name", "Network Names");
        addGUITranslation("set_name", "Set Network Name");
        addGUITranslation("password", "Network Password");
        addGUITranslation("set_password", "Set Network Password");
        addGUITranslation("known_networks", "Known Networks");
        addGUITranslation("create_network", "Create Network");
        addGUITranslation("join_network", "Join Network");
        addGUITranslation("create_settings", "Join Network?");
        addGUITranslation("join_settings", "Create Network?");
        addGUITranslation("confirm_create", "Create New Network");
        addGUITranslation("confirm_join", "Join Selected Network");
        addGUITranslation("configure_block_network", "Configure Block Network");

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