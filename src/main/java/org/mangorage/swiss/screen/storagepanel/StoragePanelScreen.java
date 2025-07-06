package org.mangorage.swiss.screen.storagepanel;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.network.MenuInteractPacketC2S;
import org.mangorage.swiss.network.RequestNetworkItemsPacketC2S;
import org.mangorage.swiss.registry.SWISSDataComponents;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;
import org.mangorage.swiss.util.NumbersUtil;
import org.mangorage.swiss.world.ItemCount;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoragePanelScreen extends AbstractContainerScreen<StoragePanelMenu> implements IUpdatable {

    private EditBox searchBox;
    private List<ItemStack> allItems;
    private List<ItemStack> filteredItems;
    private int scrollIndex = 0;

    private static final int COLUMNS = 9;
    public static int visibleRows = 12;
    private int itemsPerPage = visibleRows * COLUMNS;

    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_X_OFFSET = 174; // adjust to fit GUI width
    private static final int SCROLLBAR_Y_OFFSET = 18;
    private boolean isDraggingScrollbar = false;
    private int dragOffsetY = 0;

    private ItemStack selected = null;


    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/interface_gui.png");
    private static final ResourceLocation ROW_SPRITE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/interface_row.png");
    static final ResourceLocation SCROLL_SPRITE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/interface_scroll.png");

    public StoragePanelScreen(StoragePanelMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.itemsPerPage = visibleRows * COLUMNS;
        this.allItems = getAllItems();
        this.filteredItems = new ArrayList<>(allItems);
        this.imageHeight = 175 + (visibleRows - 3) * 18;
        this.imageWidth = 192;
        this.inventoryLabelY = this.imageHeight - 98;
    }

    @Override
    public void update() {
        this.allItems = getAllItems();
        this.filteredItems = new ArrayList<>(allItems);
    }

    public List<ItemStack> getAllItems() {
        return menu.itemStacks;
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(font, leftPos + 69 , topPos + 3, 100, 14, Component.literal("Search"));
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);
    }

    private void onSearchChanged(String text) {
        filteredItems = allItems.stream()
                .filter(item -> item.getItem().getDescription().getString().toLowerCase().contains(text.toLowerCase()))
                .toList();
        scrollIndex = 0; // Reset scroll
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int baseHeight = 72; // Height of top (non-tiling) part
        int tileHeight = 18; // Height of 1 row tile
        int extraRows = visibleRows - 3;

        int slotRowWidth = 162; // Width for the slot row sprite (adjust to your texture width)
        int slotRowHeight = 18; // Height for one slot row sprite
        int startX = leftPos + 7; // X offset for slot row sprites (adjust to align with slots)
        int startY = topPos + 19;  // Y offset where first row starts (adjust as needed)

        // --- 1. Draw top static section (first 3 rows) ---
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, baseHeight);

        // --- 2. Tile middle strip (starts at y=72 in texture) ---
        int tileSourceX = 0;
        int tileSourceY = 54; // ← This is the key fix
        int tileWidth = 192;

        for (int i = 0; i < extraRows; i++) {
            int drawY = topPos + baseHeight + (i * tileHeight);
            guiGraphics.blit(TEXTURE, leftPos, drawY, tileSourceX, tileSourceY, tileWidth, tileHeight);
        }

        // --- 3. Draw bottom GUI section (e.g., inventory slots) ---
        int bottomSourceY = baseHeight + tileHeight - 18; // starts after tiling zone
        int bottomDestY = topPos + baseHeight + (extraRows * tileHeight);
        int bottomHeight = 101; // height of bottom section in texture

        guiGraphics.blit(TEXTURE, leftPos, bottomDestY, 0, bottomSourceY, imageWidth, bottomHeight);

        // --- 4. Draw scrollbar ---
        int scrollbarX = leftPos + SCROLLBAR_X_OFFSET;
        int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;
        int handleHeight = 15;
        int maxScroll = Math.max(0, filteredItems.size() - itemsPerPage);
        if (maxScroll > 0) {
            float scrollPercent = scrollIndex / (float) maxScroll;
            int handleY = scrollbarY + (int) ((getScrollbarHeight() - handleHeight) * scrollPercent);
            guiGraphics.blit(SCROLL_SPRITE, scrollbarX, handleY + 2, 168, 0, 12, handleHeight, 12, 15);

        }

        // --- 5. Draw rows ON TOP ---
        RenderSystem.setShaderTexture(0, ROW_SPRITE); // Make sure to bind row texture
        for (int i = 0; i < visibleRows; i++) {
            int y = startY + (i * slotRowHeight);
            guiGraphics.blit(ROW_SPRITE, startX, y, 0, 0, slotRowWidth, slotRowHeight, slotRowWidth, slotRowHeight);
        }

        // Debug: draw semi-transparent red rectangle to check row positions (optional)
        // guiGraphics.fill(startX, startY, startX + slotRowWidth, startY + visibleRows * slotRowHeight, 0x80FF0000);
    }


    private int getScrollbarHeight() {
        return 18 * visibleRows;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);

        int startX = leftPos + 8;
        int startY = topPos + 20; // Adjusted to fit the GUI layoute

        Map<Item, Integer> itemTotalCounts = new LinkedHashMap<>();
        for (ItemStack stack : filteredItems) {
            itemTotalCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        List<Map.Entry<Item, Integer>> itemList = new ArrayList<>(itemTotalCounts.entrySet());

        for (int i = 0; i < itemsPerPage; i++) {
            int index = scrollIndex + i;
            if (index >= itemList.size()) break;

            Map.Entry<Item, Integer> entry = itemList.get(index);
            Item item = entry.getKey();
            int count = entry.getValue();

            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = startX + col * 18;
            int y = startY + row * 18;

            ItemStack finalTotalItemStack = new ItemStack(item, count);
            finalTotalItemStack.set(SWISSDataComponents.ITEM_COUNT.get(), new ItemCount(finalTotalItemStack.getCount()));

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(font, finalTotalItemStack, mouseX, mouseY);
                selected = finalTotalItemStack;
            } else {
                selected = null;
            }
            guiGraphics.renderItem(finalTotalItemStack, x, y);
            renderAmount(guiGraphics, x, y, NumbersUtil.format(finalTotalItemStack.getCount()), 0xFFFFFF);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static void renderAmount(final GuiGraphics graphics, final int x, final int y, final String text, final int color) {
        renderAmount(graphics, x, y, text, color, text.length() <= 3);
    }

    public static void renderAmount(final GuiGraphics graphics, final int x, final int y, final String text, final int color, final boolean large) {
        final Font font = Minecraft.getInstance().font;
        final PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        // Large amounts overlap with the slot lines (see Minecraft behavior)
        poseStack.translate(x + (large ? 1D : 0D), y + (large ? 1D : 0D), 300);
        if (!large) {
            poseStack.scale(0.5F, 0.5F, 1);
        }
        graphics.drawString(font, text, (large ? 16 : 30) - font.width(text), large ? 8 : 22, color, true);
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollbarX = leftPos + SCROLLBAR_X_OFFSET;
        int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;
        int handleHeight = 15;

        int maxScroll = Math.max(0, filteredItems.size() - itemsPerPage);
        if (maxScroll > 0) {
            float scrollPercent = scrollIndex / (float) maxScroll;
            int handleY = scrollbarY + (int) ((getScrollbarHeight() - handleHeight) * scrollPercent);

            if (MouseUtil.isMouseOver(mouseX, mouseY, scrollbarX, handleY, SCROLLBAR_WIDTH, handleHeight)) {
                isDraggingScrollbar = true;
                dragOffsetY = (int) mouseY - handleY;
                return true;
            } else {
                isDraggingScrollbar = false;
            }
        }

        if (selected != null) {
            Minecraft.getInstance().player.connection.send(new MenuInteractPacketC2S(selected, ClickType.PICKUP.ordinal()));
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void containerTick() {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.connection.send(RequestNetworkItemsPacketC2S.INSTANCE);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollbar) {
            int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;
            int handleHeight = 15;
            int maxScroll = Math.max(0, filteredItems.size() - itemsPerPage);

            int relativeY = (int) mouseY - scrollbarY - dragOffsetY;
            float percent = Mth.clamp(relativeY / (float)(getScrollbarHeight() - handleHeight), 0.0F, 1.0F);

            int rawIndex = (int)(percent * maxScroll);
            scrollIndex = Mth.clamp((rawIndex / COLUMNS) * COLUMNS, 0, maxScroll);

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, filteredItems.size() - itemsPerPage);
        if (maxScroll > 0) {
            // Scroll by whole rows
            int newScrollIndex = scrollIndex - (int) scrollY * COLUMNS;
            // Clamp within bounds
            newScrollIndex = Mth.clamp(newScrollIndex, 0, maxScroll);
            // Snap to multiple of COLUMNS (whole rows)
            scrollIndex = (newScrollIndex / COLUMNS) * COLUMNS;
            return true;
        }
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
