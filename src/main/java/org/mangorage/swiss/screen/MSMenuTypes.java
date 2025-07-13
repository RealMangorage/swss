package org.mangorage.swiss.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.interfaces.config_block.ConfigureBlockNetworkMenu;
import org.mangorage.swiss.screen.interfaces.exporter.ExporterMenu;
import org.mangorage.swiss.screen.interfaces.importer.ImporterMenu;
import org.mangorage.swiss.screen.misc.manager.ManagerMenu;
import org.mangorage.swiss.screen.misc.network.NetworkMenu;
import org.mangorage.swiss.screen.misc.setting.SettingsMenu;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;
import org.mangorage.swiss.screen.panels.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.screen.misc.test.TestMenu;

public final class MSMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, SWISS.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StoragePanelMenu>> STORAGE_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<CraftingPanelMenu>> CRAFTING_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<TestMenu>> TEST_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<SettingsMenu>> SETTINGS_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<ExporterMenu>> EXPORTER_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<ImporterMenu>> IMPORTER_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<NetworkMenu>> NETWORK_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<ManagerMenu>> MANAGER_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<ConfigureBlockNetworkMenu>> CONFIGURE_BLOCK_NETWORK_MENU;

    static {
        STORAGE_MENU = MENUS.register("storage_menu", () -> IMenuTypeExtension.create(StoragePanelMenu::new));
        CRAFTING_MENU = MENUS.register("crafting_menu", () -> IMenuTypeExtension.create(CraftingPanelMenu::new));
        TEST_MENU = MENUS.register("test_menu", () -> IMenuTypeExtension.create(TestMenu::new));
        SETTINGS_MENU = MENUS.register("settings_menu", () -> IMenuTypeExtension.create(SettingsMenu::new));
        EXPORTER_MENU = MENUS.register("exporter_menu", () -> IMenuTypeExtension.create(ExporterMenu::new));
        IMPORTER_MENU = MENUS.register("importer_menu", () -> IMenuTypeExtension.create(ImporterMenu::new));
        NETWORK_MENU = MENUS.register("network_menu", () -> IMenuTypeExtension.create(NetworkMenu::new));
        MANAGER_MENU = MENUS.register("manager_menu", () -> IMenuTypeExtension.create(ManagerMenu::new));
        CONFIGURE_BLOCK_NETWORK_MENU = MENUS.register("configure_block_network_menu", () -> IMenuTypeExtension.create(ConfigureBlockNetworkMenu::new));
    }
}
