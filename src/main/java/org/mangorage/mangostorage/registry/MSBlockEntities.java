package org.mangorage.mangostorage.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.mangostorage.MangoStorage;
import org.mangorage.mangostorage.world.block.entity.ExporterBlockEntity;
import org.mangorage.mangostorage.world.block.entity.InterfaceBlockEntity;
import org.mangorage.mangostorage.world.block.entity.TestInterfaceBlockEntity;

public final class MSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MangoStorage.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InterfaceBlockEntity>> INTERFACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("interface", () -> BlockEntityType.Builder.of(
            InterfaceBlockEntity::new,
            MSBlocks.INTERFACE_BLOCK.get()
    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExporterBlockEntity>> EXPORTER_BLOCK_ENTITY = BLOCK_ENTITIES.register("exporter", () -> BlockEntityType.Builder.of(
            ExporterBlockEntity::new,
            MSBlocks.EXPORTER_BLOCK.get()
    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TestInterfaceBlockEntity>> TEST_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_interface", () -> BlockEntityType.Builder.of(
            TestInterfaceBlockEntity::new,
            MSBlocks.TEST_INTERFACE_BLOCK.get()
    ).build(null));




    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
