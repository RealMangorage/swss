package org.mangorage.swiss.screen.network;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.util.MousePositionManagerUtil;
import org.mangorage.swiss.storage.util.IUpdatable;

import java.util.List;

public class NetworkScreen extends AbstractContainerScreen<NetworkMenu> implements IUpdatable {

    private final List<String> stringList = List.of(
            "network1", "network2", "network3", "network4", "network5", "network6"
    );

    private int scrollOffset = 0;
    private final int maxVisibleLines = 3; // Adjust depending on UI height
    private final int lineHeight = 12;



    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");

    public NetworkScreen(NetworkMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 165;
        this.imageWidth = 175;
    }

    @Override
    public void update() {
    }

    @Override
    protected void init() {
        super.init();
        MousePositionManagerUtil.setLastKnownPosition();
    }

    @Override
    public void onClose() {
        super.onClose();
        MousePositionManagerUtil.clear();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int x = leftPos + 10;
        int y = topPos + 30;

        int listSize = stringList.size();
        int end = Math.min(scrollOffset + maxVisibleLines, listSize);

        for (int i = scrollOffset; i < end; i++) {
            guiGraphics.drawString(font, stringList.get(i), x, y + (i - scrollOffset) * lineHeight, 0xFFFFFF);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MousePositionManagerUtil.getLastKnownPosition();

        return super.mouseClicked(mouseX, mouseY, button);
    }



    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxOffset = Math.max(0, stringList.size() - maxVisibleLines);
        if (scrollY < 0) { // Scrolling down
            scrollOffset = Math.min(scrollOffset + 1, maxOffset);
        } else if (scrollY > 0) { // Scrolling up
            scrollOffset = Math.max(scrollOffset - 1, 0);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


}
