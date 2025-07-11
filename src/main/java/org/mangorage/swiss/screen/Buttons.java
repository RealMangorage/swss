package org.mangorage.swiss.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.client.button.Button;
import org.mangorage.swiss.client.button.ButtonStack;
import org.mangorage.swiss.network.MenuInteractPacketC2S;

public final class Buttons {
    public static final ButtonStack DEFAULT_INTERFACE = new ButtonStack.Builder()
            .addButton(
                    new Button(
                            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_block.png"),
                            0, 2,
                            0, 0,
                            17, 17,
                            17, 17,
                            button -> {
                                Minecraft.getInstance().player.connection
                                        .send(
                                                new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 1)
                                        );
                            },
                            (guiGraphics, font, mouseX, mouseY, x, y) -> {
                                guiGraphics.renderTooltip(font, Component.translatable("gui.swiss.settings_menu"), mouseX, mouseY);
                            }
                    )
            )
            .addButton(
                    new Button(
                            ResourceLocation.fromNamespaceAndPath(SWISS.MODID, "textures/gui/button_network.png"),
                            0, 22,
                            0, 0,
                            17, 17,
                            17, 17,
                            button -> {
                                Minecraft.getInstance().player.connection
                                        .send(
                                                new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 2)
                                        );
                            },
                            (guiGraphics, font, mouseX, mouseY, x, y) -> {
                                guiGraphics.renderTooltip(font, Component.translatable("gui.swiss.configure_block_network"), mouseX, mouseY);
                            }
                    )
            )
            .build();
}
