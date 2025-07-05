package org.mangorage.swiss.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;

public final class MSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(SWISS.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, SWISS.MODID);

    public static final DeferredHolder<Item, BlockItem> STORAGE_ITEM_INTERFACE_ITEM = ITEMS.register("storage_item_interface", () -> new BlockItem(
            MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, BlockItem> EXPORTER_ITEM_INTERFACE_ITEM = ITEMS.register("exporter_item_interface", () -> new BlockItem(
            MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, Item> STORAGE_ITEM_PANEL_ITEM = ITEMS.register("storage_item_panel", () -> new BlockItem(
            MSBlocks.STORAGE_ITEM_PANEL_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, Item> TEST_BLOCK_ITEM = ITEMS.register("test_block_item", () -> new BlockItem(
            MSBlocks.TEST_BLOCK.get(),
            new Item.Properties()
    ));



    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(SWISS.MODID, () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(STORAGE_ITEM_INTERFACE_ITEM.get(), 1))
            .title(Component.translatable("itemGroup." + SWISS.MODID))
            .displayItems((p, o) -> {
                o.accept(STORAGE_ITEM_INTERFACE_ITEM.get());
                o.accept(EXPORTER_ITEM_INTERFACE_ITEM.get());
                o.accept(STORAGE_ITEM_PANEL_ITEM.get());
                o.accept(TEST_BLOCK_ITEM.get());

            })
            .build()
    );


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
