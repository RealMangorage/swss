package org.mangorage.swiss.screen.importer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/*
public class ImporterScreen extends AbstractContainerScreen<ImporterMenu> implements IUpdatable {

    private EditBox searchBox;
    private List<ItemStack> allItems;
    private List<ItemStack> filteredItems;
    private int scrollIndex = 0; // starting item index
    private static final int ITEMS_PER_PAGE = 24;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 54; // 3 rows * 18px each
    private static final int SCROLLBAR_X_OFFSET = 154; // adjust to fit GUI width
    private static final int SCROLLBAR_Y_OFFSET = 16;
    private boolean isDraggingScrollbar = false;
    private int dragOffsetY = 0;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/import_export_gui.png");

    public ImporterScreen(ImporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.allItems = getAllItems();
        this.filteredItems = new ArrayList<>(allItems);
        this.imageHeight = 165;
        this.imageWidth = 175;
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
        searchBox = new EditBox(font, leftPos + 3 , topPos + 3, 120, 14, Component.literal("Search"));
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
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int scrollbarX = leftPos + SCROLLBAR_X_OFFSET;
        int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;

        int handleHeight = 15;
        int maxScroll = Math.max(0, filteredItems.size() - ITEMS_PER_PAGE);
        if (maxScroll > 0) {
            float scrollPercent = scrollIndex / (float) maxScroll;
            int handleY = scrollbarY + (int) ((SCROLLBAR_HEIGHT - handleHeight) * scrollPercent);
            guiGraphics.blit(TEXTURE, scrollbarX, handleY, 176, 0, 12, 15);  // handle
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);

        int startX = leftPos + 8;
        int startY = topPos + 18;

        Map<Item, Integer> itemTotalCounts = new LinkedHashMap<>();
        for (ItemStack stack : filteredItems) {
            itemTotalCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        List<Map.Entry<Item, Integer>> itemList = new ArrayList<>(itemTotalCounts.entrySet());

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = scrollIndex + i;
            if (index >= itemList.size()) break;

            Map.Entry<Item, Integer> entry = itemList.get(index);
            Item item = entry.getKey();
            int count = entry.getValue();

            int row = i / 8;
            int col = i % 8;
            int x = startX + col * 18;
            int y = startY + row * 18;

            ItemStack finalTotalItemStack = new ItemStack(item, count);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(font, finalTotalItemStack, mouseX, mouseY);
            }
            guiGraphics.renderItem(finalTotalItemStack, x, y);
            guiGraphics.renderItemDecorations(font, finalTotalItemStack, x, y);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollbarX = leftPos + SCROLLBAR_X_OFFSET;
        int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;
        int handleHeight = 15;

        int maxScroll = Math.max(0, filteredItems.size() - ITEMS_PER_PAGE);
        if (maxScroll > 0) {
            float scrollPercent = scrollIndex / (float) maxScroll;
            int handleY = scrollbarY + (int) ((SCROLLBAR_HEIGHT - handleHeight) * scrollPercent);

            // Use MouseUtil to check if the mouse is over the scrollbar handle
            if (MouseUtil.isMouseOver(mouseX, mouseY, scrollbarX, handleY, SCROLLBAR_WIDTH, handleHeight)) {
                isDraggingScrollbar = true;
                dragOffsetY = (int) mouseY - handleY;
                return true;
            } else {
                isDraggingScrollbar = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }



    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollbar) {
            int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;
            int handleHeight = 15;
            int maxScroll = Math.max(0, filteredItems.size() - ITEMS_PER_PAGE);

            int relativeY = (int) mouseY - scrollbarY - dragOffsetY;
            float percent = Mth.clamp(relativeY / (float)(SCROLLBAR_HEIGHT - handleHeight), 0.0F, 1.0F);
            scrollIndex = Mth.clamp((int)(percent * maxScroll), 0, maxScroll);

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, filteredItems.size() - ITEMS_PER_PAGE);
        if (maxScroll > 0) {
            scrollIndex = Mth.clamp(scrollIndex - (int)scrollY * 8, 0, maxScroll); // scroll 8 items per notch
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

 */
