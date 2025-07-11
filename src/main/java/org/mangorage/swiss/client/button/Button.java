package org.mangorage.swiss.client.button;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record Button(
        ResourceLocation buttonRL,
        int x, int y,
        int uOffset, int vOffset,
        int width, int height,
        int textureWidth, int textureHeight,
        Consumer<Integer> clickAction,
        IRenderTooltip renderTooltip
) {

    public interface IRenderTooltip {
        void render(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, int x, int y);
    }

    public void blit(GuiGraphics guiGraphics) {
        guiGraphics.blit(buttonRL(), x(), y(), uOffset(), vOffset(), width, height, textureWidth, textureHeight);
    }

    public void blit(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(buttonRL(), x() + x, y() + y, uOffset(), vOffset(), width, height, textureWidth, textureHeight);
    }
}
