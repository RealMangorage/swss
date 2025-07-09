package org.mangorage.swiss.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.world.item.SettingsItems;

public final class SWISSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(SWISS.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, SWISS.MODID);

    public static final DeferredHolder<Item, BlockItem> STORAGE_ITEM_INTERFACE_ITEM = ITEMS.register("storage_item_interface", () -> new BlockItem(
            SWISSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, BlockItem> EXPORTER_ITEM_INTERFACE_ITEM = ITEMS.register("exporter_item_interface", () -> new BlockItem(
            SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, BlockItem> IMPORTER_ITEM_INTERFACE_ITEM = ITEMS.register("importer_item_interface", () -> new BlockItem(
            SWISSBlocks.IMPORTER_ITEM_INTERFACE_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, Item> STORAGE_ITEM_PANEL_ITEM = ITEMS.register("storage_item_panel", () -> new BlockItem(
            SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, Item> SWISS_CHEESE = ITEMS.register("swiss_cheese", () -> new Item(
            new Item.Properties()
                    .component(
                            DataComponents.FOOD,
                            new FoodProperties.Builder()
                                    .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 12, 100), 0.000000000000000001f)
                                    .saturationModifier(0.1f)
                                    .usingConvertsTo(Items.STICK) // There is a hidden stick!
                                    .build()
                            )
    ));

    public static final DeferredHolder<Item, Item> TEST_BLOCK_ITEM = ITEMS.register("test_block_item", () -> new BlockItem(
            SWISSBlocks.TEST_BLOCK.get(),
            new Item.Properties()
    ));

    public static final DeferredHolder<Item, Item> SETTINGS = ITEMS.register("settings", () -> new SettingsItems(
            new Item.Properties()
    ));



    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(SWISS.MODID, () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(SWISS_CHEESE.get(), 1))
            .title(Component.translatable("itemGroup." + SWISS.MODID))
            .displayItems((p, o) -> {
                o.accept(STORAGE_ITEM_INTERFACE_ITEM.get());
                o.accept(EXPORTER_ITEM_INTERFACE_ITEM.get());
                o.accept(IMPORTER_ITEM_INTERFACE_ITEM.get());
                o.accept(STORAGE_ITEM_PANEL_ITEM.get());
                o.accept(SWISS_CHEESE.get());

                if (p.hasPermissions()) {
                    o.accept(TEST_BLOCK_ITEM.get());
                    o.accept(SETTINGS.get());
                }
            })
            .build()
    );


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
