package org.mangorage.swiss.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.swiss.SWISS;

public final class MSMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, SWISS.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StoragePanelMenu>> STORAGE_MENU;

    static {
        STORAGE_MENU = MENUS.register("storage_menu", () -> IMenuTypeExtension.create(StoragePanelMenu::new));
    }
}
