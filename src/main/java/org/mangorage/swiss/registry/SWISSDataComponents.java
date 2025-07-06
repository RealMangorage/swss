package org.mangorage.swiss.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.world.ItemCount;

public final class SWISSDataComponents {
    public static DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, SWISS.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ItemCount>> ITEM_COUNT = COMPONENTS.register("item_count", () ->
            DataComponentType.<ItemCount>builder()
                    .persistent(ItemCount.CODEC)
                    .networkSynchronized(ItemCount.STREAM_CODEC)
                    .build()
            );

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }
}
