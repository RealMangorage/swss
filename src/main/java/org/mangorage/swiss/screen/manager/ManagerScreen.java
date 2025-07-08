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
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;

import java.util.List;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> implements IUpdatable {

    private EditBox createNetworkNameEditBox;
    private EditBox createNetworkPasswordEditBox;
    private EditBox joinNetworkPasswordEditBox;
    public int textAdjust = 8;
    private int networkScrollIndex = 0;
    private final int VISIBLE_NETWORKS = 3;

    private List<String> knownNetworks = List.of(
            "ben", "mango", "ec2t", "asdasd", "23423", "asdasdas");

    private int managerButtonX = 155;
    private int managerButtonY = 4;
    private int confirmButtonX = 155;
    private int confirmButtonY = 55;


    private ManagerModes managerModes = ManagerModes.CREATE;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/settings_gui.png");
    private static final ResourceLocation BUTTON_JOIN =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_manager_join.png");
    private static final ResourceLocation BUTTON_CREATE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_manager_create.png");
    private static final ResourceLocation BUTTON_CONFIRM =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_confirm.png");

    public ManagerScreen(ManagerMenu menu, Inventory inventory, Component component) {
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
        this.clearWidgets();

        if (managerModes == ManagerModes.CREATE) {
            createNetworkNameEditBox = new EditBox(font, leftPos + textAdjust, topPos + 28, 80, 14, Component.translatable("gui.swiss.name"));
            addRenderableWidget(createNetworkNameEditBox);

            createNetworkPasswordEditBox = new EditBox(font, leftPos + textAdjust, topPos + 56, 80, 14, Component.translatable("gui.swiss.password"));
            addRenderableWidget(createNetworkPasswordEditBox);
        }

        if (managerModes == ManagerModes.JOIN) {
            joinNetworkPasswordEditBox = new EditBox(font, leftPos + textAdjust, topPos + 56, 80, 14, Component.translatable("gui.swiss.join_password"));
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

            int yOffset = 22;
            int maxIndex = Math.min(networkScrollIndex + VISIBLE_NETWORKS, knownNetworks.size());

            for (int i = networkScrollIndex; i < maxIndex; i++) {
                guiGraphics.drawString(
                        font,
                        Component.literal(knownNetworks.get(i)).withStyle(style -> style.withUnderlined(true)),
                        leftPos + textAdjust + 5,
                        topPos + yOffset,
                        4210752,
                        false
                );
                yOffset += font.lineHeight + 2;
            }
        }



        int x = leftPos + 10;
        int y = topPos + 30;

        renderTooltip(guiGraphics, mouseX, mouseY);
        renderButtonTooltips(guiGraphics, mouseX, mouseY, leftPos, topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

        } else if (managerModes == ManagerModes.JOIN) {
            if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + managerButtonX, topPos + managerButtonY, 0, 0, 17, 17)) {
                managerModes = ManagerModes.CREATE;
                init();
            } else if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + confirmButtonX, topPos + confirmButtonY, 0, 0, 17, 17)) {

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

        if (createNetworkNameEditBox.isFocused()) {
            if (createNetworkNameEditBox.keyPressed(keyCode, scanCode, modifiers) || createNetworkNameEditBox.canConsumeInput()) {
                return true;
            }
        }

        if (createNetworkPasswordEditBox.isFocused()) {
            if (createNetworkPasswordEditBox.keyPressed(keyCode, scanCode, modifiers) || createNetworkPasswordEditBox.canConsumeInput()) {
                return true;
            }
        }

        if (joinNetworkPasswordEditBox.isFocused()) {
            if (joinNetworkPasswordEditBox.keyPressed(keyCode, scanCode, modifiers) || joinNetworkPasswordEditBox.canConsumeInput()) {
                return true;
            }
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
