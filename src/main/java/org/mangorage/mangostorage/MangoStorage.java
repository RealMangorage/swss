package org.mangorage.mangostorage;

import com.mojang.logging.LogUtils;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerLifecycleEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.mangorage.mangostorage.network.StorageNetworkManager;
import org.mangorage.mangostorage.registry.MSBlockEntities;
import org.mangorage.mangostorage.registry.MSBlocks;
import org.mangorage.mangostorage.registry.MSItems;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MangoStorage.MODID)
public final class MangoStorage {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mangostorage";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public MangoStorage(IEventBus modEventBus, ModContainer modContainer) {
        MSBlocks.register(modEventBus);
        MSBlockEntities.register(modEventBus);
        MSItems.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(MangoStorage::serverStarting);
        NeoForge.EVENT_BUS.addListener(MangoStorage::serverStopped);
    }

    public static void serverStarting(ServerStartingEvent event) {
        StorageNetworkManager.start();
    }

    public static void serverStopped(ServerStoppedEvent event) {
        StorageNetworkManager.stop();
    }
}
