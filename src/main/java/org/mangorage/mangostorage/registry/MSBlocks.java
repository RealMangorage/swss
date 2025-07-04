package org.mangorage.mangostorage.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.world.block.StorageBlock;
import org.mangorage.mangostorage.world.block.TestInferfaceBlock;
import org.mangorage.mangostorage.world.block.entity.ExporterBlockEntity;
import org.mangorage.mangostorage.world.block.entity.InterfaceBlockEntity;
import org.mangorage.mangostorage.world.block.entity.TestInterfaceBlockEntity;

public final class MSBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MangoStorage.MODID);

    public static final DeferredHolder<Block, StorageBlock> INTERFACE_BLOCK = BLOCKS.register("interface", () -> new StorageBlock(
            BlockBehaviour.Properties.of(), InterfaceBlockEntity::new
    ));
    public static final DeferredHolder<Block, StorageBlock> EXPORTER_BLOCK = BLOCKS.register("exporter", () -> new StorageBlock(
            BlockBehaviour.Properties.of(), ExporterBlockEntity::new
    ));

    public static final DeferredHolder<Block, Block> TEST_INTERFACE_BLOCK  = BLOCKS.register("test_interface", () -> new TestInferfaceBlock(
            BlockBehaviour.Properties.of(), TestInterfaceBlockEntity::new
    ));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
