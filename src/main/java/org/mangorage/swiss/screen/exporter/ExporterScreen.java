package org.mangorage.swiss.screen.exporter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import org.mangorage.swiss.network.MenuInteractPacketC2S;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.util.MouseUtil;

import java.util.*;

public class ExporterScreen extends AbstractContainerScreen<ExporterMenu> implements IUpdatable {

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
    int filterY = 20;
    int upgradeY = 49;

    private int settingsButtonX = 0;
    private int settingsButtonY = 2;

    private List<ItemStack> filterItems = new ArrayList<>();
    private List<ItemStack> upgradeItems = new ArrayList<>();
    private ItemStack selectedFilter = ItemStack.EMPTY;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/import_export_gui.png");
    static final ResourceLocation SETTINGS_BUTTON =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/button_settings.png");

    public ExporterScreen(ExporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.allItems = getAllItems();
        this.filteredItems = new ArrayList<>(allItems != null ? allItems : List.of());
        this.imageHeight = 165;
        this.imageWidth = 209;
        this.inventoryLabelX = 25;
        this.titleLabelX = 25;

        while (filterItems.size() < 9) {
            filterItems.add(ItemStack.EMPTY);
        }

        while (upgradeItems.size() < 9) {
            upgradeItems.add(ItemStack.EMPTY);
        }
    }

    @Override
    public void update() {
        this.allItems = getAllItems();
        this.filteredItems = new ArrayList<>(allItems != null ? allItems : List.of());

        // Also sync filterItems from blockEntity as we discussed:
        filterItems.clear();
        filterItems.addAll(menu.getBlockEntity().getExportItems());

        while (filterItems.size() < 9) {
            filterItems.add(ItemStack.EMPTY);
        }
    }


    public List<ItemStack> getAllItems() {
        return menu.itemStacks;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int scrollbarX = leftPos + SCROLLBAR_X_OFFSET + 17;
        int scrollbarY = topPos + SCROLLBAR_Y_OFFSET;

        int handleHeight = 15;
        int maxScroll = Math.max(0, (filteredItems != null ? filteredItems.size() : 0) - ITEMS_PER_PAGE);
        if (maxScroll > 0) {
            float scrollPercent = scrollIndex / (float) maxScroll;
            int handleY = scrollbarY + (int) ((SCROLLBAR_HEIGHT - handleHeight) * scrollPercent);
            guiGraphics.blit(TEXTURE, scrollbarX, handleY, 176, 0, 12, 15);  // handle
        }
        guiGraphics.blit(SETTINGS_BUTTON, leftPos, topPos + 2, 0, 0, 17, 17, 17, 17);

    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int startX = leftPos + 8;
        int startY = topPos + 18;

        // Render first row: filter items (9 across)
        for (int i = 0; i < filterItems.size(); i++) {
            int x = startX + i * 18  + 17;
            int y = topPos + filterY;  // position near top, adjust as needed

            ItemStack filterStack = filterItems.get(i);
            guiGraphics.renderItem(filterStack, x, y);
            guiGraphics.renderItemDecorations(font, filterStack, x, y);

            if (!selectedFilter.isEmpty() && ItemStack.isSameItem(filterStack, selectedFilter)) {
                // Draw a highlight rectangle around the selected filter
                guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, 0xAAFFFFFF);
            }

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(font, filterStack, mouseX, mouseY);
            }
            renderButtonTooltips(guiGraphics, mouseX, mouseY, leftPos, topPos);
        }

        // Render second row: upgrade items (9 across)
        for (int i = 0; i < upgradeItems.size(); i++) {
            int x = startX + i * 18 + 17;
            int y = topPos + upgradeY;  // below the filter row, adjust as needed

            ItemStack upgradeStack = upgradeItems.get(i);
            guiGraphics.renderItem(upgradeStack, x, y);
            guiGraphics.renderItemDecorations(font, upgradeStack, x, y);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(font, upgradeStack, mouseX, mouseY);
            }
        }

        // Shift main items down by 2 rows (36px) to accommodate new rows
        int mainItemsStartY = startY + 36;

        // Existing main item rendering (adjust y positions)
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
            int y = mainItemsStartY + row * 18;

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
        int startX = leftPos + 25;

        // Get the item currently held on cursor
        ItemStack heldStack = this.getMenu().getCarried();  // "getCarried()" is AbstractContainerScreen method returning the stack under the mouse

        // Check clicks on filter items row
        for (int i = 0; i < filterItems.size(); i++) {
            int x = startX + i * 18;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= filterY + topPos && mouseY < filterY + 16 + topPos) {
                // If player is holding an item, assign it to this filter slot
                if (!heldStack.isEmpty()) {
                    ItemStack newFilter = heldStack.copy();
                    newFilter.setCount(1);
                    filterItems.set(i, newFilter);
                    selectedFilter = newFilter;
                    applyFilter(selectedFilter);
                } else {
                    filterItems.set(i, ItemStack.EMPTY);
                    selectedFilter = ItemStack.EMPTY;
                    applyFilter(selectedFilter);
                }

                menu.getBlockEntity().getExportItems().clear();
                menu.getBlockEntity().getExportItems().addAll(filterItems.stream().filter(stack -> !stack.isEmpty()).toList());

                return true;
            }
        }

        // Upgrade clicks remain unchanged
        for (int i = 0; i < upgradeItems.size(); i++) {
            int x = startX + i * 18;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= upgradeY + topPos && mouseY < upgradeY + 16 + topPos) {
                onUpgradeClicked(i);
                return true;
            }
        }

        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, leftPos + settingsButtonX, topPos + settingsButtonY, 0, 0, 17, 17)) {
            Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                    new MenuInteractPacketC2S(ItemStack.EMPTY, 0, 1) // Open Settings
            );
        }

        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }


    private void applyFilter(ItemStack filter) {
        if (filter.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(itemStack -> itemStack.getItem() == filter.getItem())  // match by item type only
                    .toList();
        }
        scrollIndex = 0;
    }

    private void onUpgradeClicked(int index) {
        // Example: print upgrade clicked, you can replace with actual logic
        System.out.println("Upgrade clicked: " + index);
        // maybe send packet to server or apply upgrade effect here
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
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

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    private void renderButtonTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {

        if (MouseUtil.isMouseAboveArea(mouseX, mouseY, x, y, settingsButtonX, settingsButtonY, 17, 17)) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.swiss.settings_menu"), mouseX, mouseY);
        }
    }

}
