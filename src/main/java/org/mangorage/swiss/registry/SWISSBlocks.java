package org.mangorage.swiss.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
import org.mangorage.swiss.world.block.PanelNetworkBlock;
import org.mangorage.swiss.world.block.TestBlock;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemExporterBlockEntity;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemImporterBlockEntity;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemInterfaceBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.TestBlockEntity;

public final class SWISSBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(SWISS.MODID);

    public static final DeferredHolder<Block, InterfaceNetworkBlock> STORAGE_ITEM_INTERFACE_BLOCK = BLOCKS.register("storage_item_interface", () -> new InterfaceNetworkBlock(
            BlockBehaviour.Properties.of()
                    .noOcclusion(),
            ItemInterfaceBlockEntity::new
    ));

    public static final DeferredHolder<Block, InterfaceNetworkBlock> EXPORTER_ITEM_INTERFACE_BLOCK = BLOCKS.register("exporter_item_interface", () -> new InterfaceNetworkBlock(
            BlockBehaviour.Properties.of()
                    .noOcclusion(),
            ItemExporterBlockEntity::new
    ));

    public static final DeferredHolder<Block, InterfaceNetworkBlock> IMPORTER_ITEM_INTERFACE_BLOCK = BLOCKS.register("importer_item_interface", () -> new InterfaceNetworkBlock(
            BlockBehaviour.Properties.of()
                    .noOcclusion(),
            ItemImporterBlockEntity::new
    ));

    public static final DeferredHolder<Block, Block> STORAGE_ITEM_PANEL_BLOCK = BLOCKS.register("storage_item_panel", () -> new PanelNetworkBlock(
            BlockBehaviour.Properties.of()
                    .noOcclusion(),
            StorageItemPanelBlockEntity::new
    ));

    public static final DeferredHolder<Block, Block> TEST_BLOCK = BLOCKS.register("test", () -> new TestBlock(
            BlockBehaviour.Properties.of(), TestBlockEntity::new
    ));


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
