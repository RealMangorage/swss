package org.mangorage.swiss.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.screen.test.TestMenu;

public final class MSMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, SWISS.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StoragePanelMenu>> STORAGE_MENU;
    public static final DeferredHolder<MenuType<?>, MenuType<TestMenu>> TEST_MENU;

    static {
        STORAGE_MENU = MENUS.register("storage_menu", () -> IMenuTypeExtension.create(StoragePanelMenu::new));
        TEST_MENU = MENUS.register("test_menu", () -> IMenuTypeExtension.create(TestMenu::new));
    }
}
