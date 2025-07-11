package org.mangorage.swiss.screen.misc.setting;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.network.MenuInteractPacketC2S;
import org.mangorage.swiss.util.MousePositionManagerUtil;
import org.mangorage.swiss.storage.device.INetworkHolder;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;
public final class SettingsScreen extends AbstractContainerScreen<SettingsMenu> implements IUpdatable {

    private int networkButtonX = 10;
    private int networkButtonY = 22;
    private int managerButtonX = 32;
    private int managerButtonY = 22;
    private int blockSettingsX = 54;
    private int blockSettingsY = 22;

    private BlockPos menuOpenPosition;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");
    static final ResourceLocation NETWORK_BUTTON =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_network.png");
    static final ResourceLocation MANAGER_BUTTON =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_manager.png");
    static final ResourceLocation BLOCK_BUTTON =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_block.png");
    static final ResourceLocation SCROLL_SPRITE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/interface_scroll.png");

    public SettingsScreen(SettingsMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 165;
        this.imageWidth = 175;

    }

    @Override
    public void update() {
    }

    @Override
    public void onClose() {
        super.onClose();
        MousePositionManagerUtil.clear();
    }

    @Override
    protected void init() {
        super.init();
        MousePositionManagerUtil.setLastKnownPosition();

        this.menuOpenPosition = this.menu.getBlockPos();
    }

    public BlockEntity getBlockEntity() {
        return menu.getLevel().getBlockEntity(menuOpenPosition);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        //Buttons
        guiGraphics.blit(NETWORK_BUTTON, leftPos + networkButtonX, topPos + networkButtonY, 0, 0, 17, 17, 17, 17);
        guiGraphics.blit(MANAGER_BUTTON, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17, 17, 17);

        if (getBlockEntity() instanceof INetworkHolder) {
            guiGraphics.blit(BLOCK_BUTTON, leftPos + blockSettingsX, topPos + blockSettingsY, 0, 0, 17, 17, 17, 17);
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        renderTooltip(guiGraphics, mouseX, mouseY);
        renderButtonTooltips(guiGraphics, mouseX, mouseY, leftPos, topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MousePositionManagerUtil.getLastKnownPosition();

        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + networkButtonX, topPos + networkButtonY, 0, 0, 17, 17)) {
            Minecraft.getInstance().getConnection().send(
                    new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 1) // Open Network
            );
        }

        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17)) {
            Minecraft.getInstance().getConnection().send(
                    new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 2) // Open Network
            );
        }
        if (getBlockEntity() instanceof INetworkHolder) {
            if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + blockSettingsX, topPos + blockSettingsY, 0, 0, 17, 17)) {
                Minecraft.getInstance().getConnection().send(
                        new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 3) // Open Network
                );
            }
        }


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

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void renderButtonTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {

        if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, networkButtonX, networkButtonY, 17, 17)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.network_settings"), mouseX, mouseY);
        }
        if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, managerButtonX, managerButtonY, 17, 17)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.create_join_network_settings"), mouseX, mouseY);
        }
        if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, blockSettingsX, blockSettingsY, 17, 17)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.configure_block_network"), mouseX, mouseY);
        }
    }

}
