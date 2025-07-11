package org.mangorage.swiss.screen.config_block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.network.MenuInteractPacketC2S;
import org.mangorage.swiss.util.MousePositionManagerUtil;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;

import java.util.List;

public class ConfigureBlockNetworkScreen extends AbstractContainerScreen<ConfigureBlockNetworkMenu> implements IUpdatable {

    private int scrollOffset = 0;
    private final int maxVisibleLines = 3; // Adjust depending on UI height
    private final int lineHeight = 12;
    public int textAdjust = 8;
    private EditBox joinNetworkPasswordEditBox;
    private int networkScrollIndex = 0;
    private final int VISIBLE_NETWORKS = 3;

    private int confirmButtonX = 155;
    private int confirmButtonY = 55;

    private NetworkInfo selectedNetwork;

    private List<NetworkInfo> knownNetworks = List.of();


    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");
    private static final ResourceLocation BUTTON_CONFIRM =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_confirm.png");

    public ConfigureBlockNetworkScreen(ConfigureBlockNetworkMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 165;
        this.imageWidth = 175;
        this.knownNetworks = menu.networkInfo;

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

        joinNetworkPasswordEditBox = new EditBox(font, leftPos + textAdjust, topPos + 56, 80, 14, Component.translatable("gui.swiss.join_password"));
        addRenderableWidget(joinNetworkPasswordEditBox);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        guiGraphics.blit(BUTTON_CONFIRM, leftPos + confirmButtonX, topPos + confirmButtonY, 0, 0, 17, 17, 17, 17);

    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int x = leftPos + 10;
        int y = topPos + 30;

        joinNetworkPasswordEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);

        int yOffset = 22;
        int maxIndex = Math.min(networkScrollIndex + VISIBLE_NETWORKS, knownNetworks.size());

        for (int i = networkScrollIndex; i < maxIndex; i++) {
            NetworkInfo info = knownNetworks.get(i);
            boolean isSelected = selectedNetwork != null && selectedNetwork.equals(info);

            Component networkText = Component.literal(info.networkName())
                    .withStyle(style -> style.withUnderlined(true).withColor(isSelected ? 0x00AAFF : 0x000000));

            guiGraphics.drawString(font, networkText, leftPos + textAdjust + 5, topPos + yOffset, 4210752, false);
            yOffset += font.lineHeight + 2;
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MousePositionManagerUtil.getLastKnownPosition();

        int yOffset = topPos + 22;
        int maxIndex = Math.min(networkScrollIndex + VISIBLE_NETWORKS, knownNetworks.size());

        for (int i = networkScrollIndex; i < maxIndex; i++) {
            int textY = yOffset;
            int textX = leftPos + textAdjust + 5;
            int textWidth = font.width(knownNetworks.get(i).networkName());
            int textHeight = font.lineHeight;

            if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, textX, textY, 0, 0, textWidth, textHeight)) {
                selectedNetwork = knownNetworks.get(i);
                return true; // Consume click
            }

            yOffset += textHeight + 2;
        }

        // Join button click
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + confirmButtonX, topPos + confirmButtonY, 0, 0, 17, 17)) {
            if (selectedNetwork != null) {
                final var data = new CompoundTag();
                data.putUUID("id", selectedNetwork.networkId());

                Minecraft.getInstance().player.connection.send(
                        new MenuInteractPacketC2S(data, 1)
                );

                Minecraft.getInstance().player.closeContainer();
            } else {
                // Optional: feedback if nothing selected
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("gui.swiss.no_network_selected"), true);
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
        int maxScroll = Math.max(0, knownNetworks.size() - VISIBLE_NETWORKS);

        if (scrollY < 0 && networkScrollIndex < maxScroll) {
            networkScrollIndex++;
        } else if (scrollY > 0 && networkScrollIndex > 0) {
            networkScrollIndex--;
        }

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

        if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, confirmButtonX, confirmButtonY, 17, 17)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.confirm_join"), mouseX, mouseY);
        }
    }
}
