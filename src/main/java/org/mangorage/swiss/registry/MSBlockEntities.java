package org.mangorage.swiss.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemExporterBlockEntity;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemInterfaceBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.TestBlockEntity;

public final class MSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SWISS.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemInterfaceBlockEntity>> STORAGE_ITEM_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("storage_item_interface_block_entity", () -> BlockEntityType.Builder.of(
            ItemInterfaceBlockEntity::new,
            MSBlocks.STORAGE_ITEM_INTERFACE_BLOCK.get()
    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemExporterBlockEntity>> EXPORTER_ITEM_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("exporter_item_interface_block_entity", () -> BlockEntityType.Builder.of(
            ItemExporterBlockEntity::new,
            MSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get()
    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StorageItemPanelBlockEntity>> STORAGE_ITEM_PANEL_BLOCK_ENTITY = BLOCK_ENTITIES.register("storage_item_panel_block_entity", () -> BlockEntityType.Builder.of(
            StorageItemPanelBlockEntity::new,
            MSBlocks.STORAGE_ITEM_PANEL_BLOCK.get()
    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block_entity", () -> BlockEntityType.Builder.of(
            TestBlockEntity::new,
            MSBlocks.TEST_BLOCK.get()
    ).build(null));





    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
