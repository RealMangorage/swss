package org.mangorage.swiss.client.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record Button(ResourceLocation buttonRL, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {

    public void blit(GuiGraphics guiGraphics) {
        guiGraphics.blit(buttonRL(), x(), y(), uOffset(), vOffset(), uWidth(), vHeight());
    }

    public void blit(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(buttonRL(), x() + x, y() + y, uOffset(), vOffset(), uWidth(), vHeight());
    }
}
