package org.mangorage.swiss;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.mangorage.swiss.screen.exporter.ExporterScreen;
import org.mangorage.swiss.screen.setting.SettingsScreen;
import org.mangorage.swiss.screen.test.TestScreen;
import org.mangorage.swiss.storage.StorageNetworkManager;
import org.mangorage.swiss.network.Packets;
import org.mangorage.swiss.registry.MSBlockEntities;
import org.mangorage.swiss.registry.MSBlocks;
import org.mangorage.swiss.registry.MSItems;
import org.mangorage.swiss.screen.storagepanel.StoragePanelScreen;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SWISS.MODID)
public final class SWISS {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "swiss";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public SWISS(IEventBus modEventBus, ModContainer modContainer) {
        MSBlocks.register(modEventBus);
        MSBlockEntities.register(modEventBus);
        MSItems.register(modEventBus);
        MSMenuTypes.MENUS.register(modEventBus);
        modEventBus.addListener(Packets::register);

        NeoForge.EVENT_BUS.addListener(SWISS::serverStarting);
        NeoForge.EVENT_BUS.addListener(SWISS::serverStopped);

    }

    public static void serverStarting(ServerStartingEvent event) {
        StorageNetworkManager.start();
    }

    public static void serverStopped(ServerStoppedEvent event) {
        StorageNetworkManager.stop();
    }

    public static ResourceLocation modRL(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    @EventBusSubscriber(modid = SWISS.MODID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MSMenuTypes.STORAGE_MENU.get(), StoragePanelScreen::new);
            event.register(MSMenuTypes.TEST_MENU.get(), TestScreen::new);
            event.register(MSMenuTypes.SETTINGS_MENU.get(), SettingsScreen::new);
            event.register(MSMenuTypes.EXPORTER_MENU.get(), ExporterScreen::new);
        }

    }
}
