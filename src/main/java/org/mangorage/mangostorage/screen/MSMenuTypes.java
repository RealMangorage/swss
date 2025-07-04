package org.mangorage.mangostorage.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mangorage.mangostorage.MangoStorage;

public class MSMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, MangoStorage.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu>> INTERFACE_MENU;

    static {
        INTERFACE_MENU = MENUS.register("interface_menu", () -> IMenuTypeExtension.create(InterfaceMenu::new));
    }
}
