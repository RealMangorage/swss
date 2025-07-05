package org.mangorage.mangostorage.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.world.block.StorageBlock;
import org.mangorage.mangostorage.world.block.StoragePanelBlock;
import org.mangorage.mangostorage.world.block.entity.item.interfaces.ItemExporterBlockEntity;
import org.mangorage.mangostorage.world.block.entity.item.interfaces.ItemInterfaceBlockEntity;
import org.mangorage.mangostorage.world.block.entity.item.panels.StorageItemPanelBlockEntity;

public final class MSBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MangoStorage.MODID);

    public static final DeferredHolder<Block, StorageBlock> STORAGE_ITEM_INTERFACE_BLOCK = BLOCKS.register("storage_item_interface", () -> new StorageBlock(
            BlockBehaviour.Properties.of(), ItemInterfaceBlockEntity::new
    ));
    public static final DeferredHolder<Block, StorageBlock> EXPORTER_ITEM_INTERFACE_BLOCK = BLOCKS.register("exporter_item_interface", () -> new StorageBlock(
            BlockBehaviour.Properties.of(), ItemExporterBlockEntity::new
    ));

    public static final DeferredHolder<Block, Block> STORAGE_ITEM_PANEL_BLOCK = BLOCKS.register("storage_item_panel", () -> new StoragePanelBlock(
            BlockBehaviour.Properties.of(), StorageItemPanelBlockEntity::new
    ));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
