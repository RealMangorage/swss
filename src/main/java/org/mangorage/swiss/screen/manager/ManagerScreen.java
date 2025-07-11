package org.mangorage.swiss.screen.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.network.CreateNetworkPacketC2S;
import org.mangorage.swiss.network.JoinNetworkPacketC2S;
import org.mangorage.swiss.util.MousePositionManagerUtil;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;

import java.util.List;

public final class ManagerScreen extends AbstractContainerScreen<ManagerMenu> implements IUpdatable {

    private EditBox createNetworkNameEditBox;
    private EditBox createNetworkPasswordEditBox;
    private EditBox joinNetworkPasswordEditBox;
    public int textAdjust = 8;
    private int networkScrollIndex = 0;
    private final int VISIBLE_NETWORKS = 3;
    private NetworkInfo selectedNetwork;

    private List<NetworkInfo> knownNetworks = List.of();

    private int managerButtonX = 155;
    private int managerButtonY = 4;
    private int confirmButtonX = 155;
    private int confirmButtonY = 55;
    private boolean draggingScrollBar = false;
    private int dragOffsetY = 0;

    private ManagerModes managerModes = ManagerModes.CREATE;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");
    private static final ResourceLocation BUTTON_JOIN =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_manager_join.png");
    private static final ResourceLocation BUTTON_CREATE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_manager_create.png");
    private static final ResourceLocation BUTTON_CONFIRM =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_confirm.png");
    static final ResourceLocation SCROLL_SPRITE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/interface_scroll.png");

    public ManagerScreen(ManagerMenu menu, Inventory inventory, Component component) {
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

        this.clearWidgets();

        if (managerModes == ManagerModes.CREATE) {
            createNetworkNameEditBox = new EditBox(font, leftPos + textAdjust, topPos + 28, 117, 14, Component.translatable("gui.swiss.name"));
            addRenderableWidget(createNetworkNameEditBox);

            createNetworkPasswordEditBox = new EditBox(font, leftPos + textAdjust, topPos + 56, 117, 14, Component.translatable("gui.swiss.password"));
            addRenderableWidget(createNetworkPasswordEditBox);
        }

        if (managerModes == ManagerModes.JOIN) {
            joinNetworkPasswordEditBox = new EditBox(font, leftPos + textAdjust, topPos + 56, 117, 14, Component.translatable("gui.swiss.join_password"));
            addRenderableWidget(joinNetworkPasswordEditBox);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (managerModes == ManagerModes.CREATE) {
            guiGraphics.blit(BUTTON_CREATE, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17, 17, 17);
        }
        if (managerModes == ManagerModes.JOIN) {
            guiGraphics.blit(BUTTON_JOIN, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17, 17, 17);
        }

        guiGraphics.blit(BUTTON_CONFIRM, leftPos + confirmButtonX, topPos + confirmButtonY, 0, 0, 17, 17, 17, 17);


    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (managerModes == ManagerModes.CREATE) {
            createNetworkNameEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
            createNetworkPasswordEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);

            guiGraphics.drawString(font, Component.translatable("gui.swiss.create_network"), leftPos + textAdjust, topPos + 6, 4210752, false);
            guiGraphics.drawString(font, Component.translatable("gui.swiss.set_name"), leftPos + textAdjust, topPos + 18, 4210752, false);
            guiGraphics.drawString(font, Component.translatable("gui.swiss.set_password"), leftPos + textAdjust, topPos + 46, 4210752, false);
        }
        if (managerModes == ManagerModes.JOIN) {
            joinNetworkPasswordEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);

            guiGraphics.drawString(font, Component.translatable("gui.swiss.join_network"), leftPos + textAdjust, topPos + 6, 4210752, false);

            // Box around the list
            int listX = leftPos + textAdjust + 3;
            int listY = topPos + 22;
            int listWidth = 100;
            int listHeight = (font.lineHeight + 2) * VISIBLE_NETWORKS;

            // Background and border
            guiGraphics.fill(listX - 2, listY - 2, listX + listWidth, listY + listHeight, 0xFFDDDDDD); // Background
            guiGraphics.fill(listX - 3, listY - 3, listX + listWidth + 1, listY - 2, 0xFF000000); // Top
            guiGraphics.fill(listX - 3, listY + listHeight, listX + listWidth + 1, listY + listHeight + 1, 0xFF000000); // Bottom
            guiGraphics.fill(listX - 3, listY - 2, listX - 2, listY + listHeight, 0xFF000000); // Left
            guiGraphics.fill(listX + listWidth, listY - 2, listX + listWidth + 1, listY + listHeight, 0xFF000000); // Right

            // Coordinates of the scrollbar box
            int scrollBoxX = listX + listWidth + 2;
            int scrollBoxWidth = 11; // enough space for visual box + sprite
            int scrollBoxY = listY - 1;
            int scrollBoxHeight = listHeight + 1;



            // Draw the list of networks
            int yOffset = 22;
            int maxIndex = Math.min(networkScrollIndex + VISIBLE_NETWORKS, knownNetworks.size());

            for (int i = networkScrollIndex; i < maxIndex; i++) {
                NetworkInfo info = knownNetworks.get(i);
                boolean isSelected = selectedNetwork != null && selectedNetwork.equals(info);

                int textX = leftPos + textAdjust + 8;
                int textY = topPos + yOffset;
                int textWidth = font.width(info.networkName());
                int textHeight = font.lineHeight;

                if (isSelected) {
                    guiGraphics.fill(textX - 2, textY - 1, textX + textWidth + 2, textY + textHeight + 1, 0xFF00AAFF);
                }

                Component networkText = Component.literal(info.networkName())
                        .withStyle(style -> style.withUnderlined(true).withColor(isSelected ? 0xFFFFFF : 0x000000));

                guiGraphics.drawString(font, networkText, textX, textY, 4210752, false);
                yOffset += font.lineHeight + 2;
            }

            // Render scroll bar if needed
            if (knownNetworks.size() > VISIBLE_NETWORKS) {

                // Draw scrollbar box (container)
                guiGraphics.fill(scrollBoxX - 1, scrollBoxY - 1, scrollBoxX + scrollBoxWidth, scrollBoxY + scrollBoxHeight + 1, 0xFFCCCCCC); // Background
                guiGraphics.fill(scrollBoxX - 2, scrollBoxY - 2, scrollBoxX + scrollBoxWidth + 1, scrollBoxY - 1, 0xFF000000); // Top border
                guiGraphics.fill(scrollBoxX - 2, scrollBoxY + scrollBoxHeight, scrollBoxX + scrollBoxWidth + 1, scrollBoxY + scrollBoxHeight + 1, 0xFF000000); // Bottom
                guiGraphics.fill(scrollBoxX - 2, scrollBoxY - 1, scrollBoxX - 1, scrollBoxY + scrollBoxHeight, 0xFF000000); // Left
                guiGraphics.fill(scrollBoxX + scrollBoxWidth, scrollBoxY - 1, scrollBoxX + scrollBoxWidth + 1, scrollBoxY + scrollBoxHeight, 0xFF000000); // Right


                int scrollBarX = listX + listWidth + 1;
                int scrollBarY = listY - 2;
                int scrollBarHeight = listHeight + 2;

                int totalNetworks = knownNetworks.size();
                int maxScroll = totalNetworks - VISIBLE_NETWORKS;

                int thumbY = scrollBarY + ((scrollBarHeight - 15) * networkScrollIndex) / maxScroll;

                // Scroll bar thumb
                guiGraphics.blit(SCROLL_SPRITE, scrollBarX, thumbY, 168, 0, 12, 15, 12, 15);
            }
        }





        int x = leftPos + 10;
        int y = topPos + 30;

        renderTooltip(guiGraphics, mouseX, mouseY);
        renderButtonTooltips(guiGraphics, mouseX, mouseY, leftPos, topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MousePositionManagerUtil.getLastKnownPosition();

        if (managerModes == ManagerModes.CREATE) {
            if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17)) {
                managerModes = ManagerModes.JOIN;
                init();
            } else if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + confirmButtonX, topPos + confirmButtonY, 0, 0, 17, 17)) {
                //create new network
                Minecraft.getInstance().player.connection.send(
                        new CreateNetworkPacketC2S(
                                createNetworkNameEditBox.getValue(),
                                createNetworkPasswordEditBox.getValue()
                        )
                );
                Minecraft.getInstance().player.closeContainer();
            }

        }else if (managerModes == ManagerModes.JOIN) {

            // Scrollbar drag start
            int listX = leftPos + textAdjust + 3;
            int listY = topPos + 22;
            int listWidth = 100;
            int listHeight = (font.lineHeight + 2) * VISIBLE_NETWORKS;

            int scrollBarX = listX + listWidth + 1;
            int scrollBarY = listY - 2;
            int scrollBarHeight = listHeight + 2;
            int totalNetworks = knownNetworks.size();
            int maxScroll = totalNetworks - VISIBLE_NETWORKS;

            if (totalNetworks > VISIBLE_NETWORKS) {
                int thumbHeight = 15;
                int thumbY = scrollBarY + ((scrollBarHeight - thumbHeight) * networkScrollIndex) / maxScroll;

                if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, scrollBarX, thumbY, 0, 0, 12, thumbHeight)) {
                    draggingScrollBar = true;
                    dragOffsetY = (int) mouseY - thumbY;
                    return true;
                }
            }

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
                    Minecraft.getInstance().player.connection.send(
                            new JoinNetworkPacketC2S(selectedNetwork.networkId(), joinNetworkPasswordEditBox.getValue())
                    );
                    Minecraft.getInstance().player.closeContainer();
                } else {
                    // Optional: feedback if nothing selected
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable("gui.swiss.no_network_selected"), true);
                }
            }

            // Switch mode button
            else if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17)) {
                managerModes = ManagerModes.CREATE;
                init();
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollBar && managerModes == ManagerModes.JOIN) {
            int listHeight = (font.lineHeight + 2) * VISIBLE_NETWORKS;
            int scrollBarY = topPos + 22;
            int scrollBarHeight = listHeight;

            int totalNetworks = knownNetworks.size();
            int maxScroll = totalNetworks - VISIBLE_NETWORKS;

            int thumbHeight = 15;
            int trackHeight = scrollBarHeight - thumbHeight;

            int newThumbY = (int) mouseY - dragOffsetY;
            newThumbY = Math.max(scrollBarY, Math.min(scrollBarY + trackHeight, newThumbY));

            int relativeY = newThumbY - scrollBarY;
            networkScrollIndex = (relativeY * maxScroll) / trackHeight;
            networkScrollIndex = Math.max(0, Math.min(maxScroll, networkScrollIndex));

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollBar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (managerModes == ManagerModes.JOIN) {
            int maxScroll = Math.max(0, knownNetworks.size() - VISIBLE_NETWORKS);

            if (scrollY < 0 && networkScrollIndex < maxScroll) {
                networkScrollIndex++;
            } else if (scrollY > 0 && networkScrollIndex > 0) {
                networkScrollIndex--;
            }

            return true; // consume the scroll event
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        boolean isFocused = createNetworkNameEditBox.isFocused() || createNetworkPasswordEditBox.isFocused() || joinNetworkPasswordEditBox.isFocused();

        if (isFocused && keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            return true;
        }


        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void renderButtonTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (managerModes == ManagerModes.CREATE) {
            if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, managerButtonX, managerButtonY, 17, 17)) {
                guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.create_settings"), mouseX, mouseY);
            }
            else if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, confirmButtonX, confirmButtonY, 17, 17)) {
                guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.confirm_create"), mouseX, mouseY);
            }
        }
        if (managerModes == ManagerModes.JOIN) {
            if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, managerButtonX, managerButtonY, 17, 17)) {
                guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.join_settings"), mouseX, mouseY);
            }
            else if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, confirmButtonX, confirmButtonY, 17, 17)) {
                guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.confirm_join"), mouseX, mouseY);
            }
        }
    }
}
