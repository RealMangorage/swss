package org.mangorage.mangostorage.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.mangostorage.MangoStorage;

public final class MSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MangoStorage.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MangoStorage.MODID);

    public static final DeferredHolder<Item, BlockItem> INTERFACE_ITEM = ITEMS.register("interface", () -> new BlockItem(
            MSBlocks.INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, BlockItem> EXPORTER_ITEM = ITEMS.register("exporter", () -> new BlockItem(
            MSBlocks.EXPORTER_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(MangoStorage.MODID, () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(INTERFACE_ITEM.get(), 1))
            .title(Component.translatable("itemGroup." + MangoStorage.MODID))
            .displayItems((p, o) -> {
                o.accept(INTERFACE_ITEM.get());
                o.accept(EXPORTER_ITEM.get());
            })
            .build()
    );


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
