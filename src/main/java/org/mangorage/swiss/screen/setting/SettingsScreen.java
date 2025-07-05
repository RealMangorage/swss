package org.mangorage.swiss.screen.setting;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.storage.util.IUpdatable;

public class SettingsScreen extends AbstractContainerScreen<SettingsMenu> implements IUpdatable {

    private EditBox searchBox;


    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");

    public SettingsScreen(SettingsMenu menu, Inventory inventory, Component component) {
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
        searchBox = new EditBox(font, leftPos + 3 , topPos + 3, 120, 14, Component.literal("Search"));
        searchBox.setValue("SETTINGS MENU");
        addRenderableWidget(searchBox);
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
        searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }



    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (searchBox.isFocused()) {
            if (searchBox.keyPressed(keyCode, scanCode, modifiers) || searchBox.canConsumeInput()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


}
