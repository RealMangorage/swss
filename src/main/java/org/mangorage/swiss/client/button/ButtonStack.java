package org.mangorage.swiss.client.button;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.mangorage.swiss.util.MouseUtil;

import java.util.ArrayList;
import java.util.List;

public final class ButtonStack {
    private final List<Button> buttons;

    public ButtonStack(List<Button> buttons) {
        this.buttons = ImmutableList.copyOf(buttons);
    }

    public void mouseClicked(int mouseX, int mouseY, int buttonClicked, int sX, int sY) {
        for (Button button : buttons) {
            if (MouseUtil.isMouseAboveArea(mouseX, mouseY, sX + button.x(), sY + button.y(), 0, 0, button.width(), button.height())) {
                button.clickAction().accept(buttonClicked);
            }
        }
    }

    public void blit(GuiGraphics guiGraphics, int leftPos, int topPos) {
        for (Button button : buttons) {
            guiGraphics.blit(
                    button.buttonRL(),
                    leftPos + button.x(),
                    topPos + button.y(),
                    button.uOffset(),
                    button.vOffset(),
                    button.width(),
                    button.height(),
                    button.textureWidth(),
                    button.textureWidth()
            );
        }
    }

    public void renderButtonTooltips(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, int x, int y) {
        for (Button button : buttons) {
            if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, button.x(), button.y(), button.width(), button.height())) {
                button.renderTooltip().render(
                        guiGraphics, font, mouseX, mouseY, x, y
                );
            }
        }
    }

    public static final class Builder {
        private final List<Button> buttons = new ArrayList<>();

        public Builder addButton(Button button) {
            buttons.add(button);
            return this;
        }

        public Builder add(ButtonStack stack) {
            buttons.addAll(stack.buttons);
            return this;
        }

        public ButtonStack build() {
            return new ButtonStack(buttons);
        }
    }
}
