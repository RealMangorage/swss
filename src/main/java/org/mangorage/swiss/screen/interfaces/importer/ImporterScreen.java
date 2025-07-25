package org.mangorage.swiss.screen.interfaces.importer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.network.SyncFilterItemsPacketC2S;
import org.mangorage.swiss.screen.Buttons;
import org.mangorage.swiss.util.MousePositionManagerUtil;
import org.mangorage.swiss.storage.util.IUpdatable;

import java.util.*;

public final class ImporterScreen extends AbstractContainerScreen<ImporterMenu> implements IUpdatable {

    int filterY = 20;
    int upgradeY = 49;

    private List<ItemStack> filterItems = new ArrayList<>();
    private List<ItemStack> upgradeItems = new ArrayList<>();
    private ItemStack selectedFilter = ItemStack.EMPTY;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SWISS.MODID,"textures/gui/import_export_gui.png");

    public ImporterScreen(ImporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
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
        while (filterItems.size() < 9) {
            filterItems.add(ItemStack.EMPTY);
        }
    }

    public void syncFilterItems() {

        Map<Integer, ItemStack> filterItemsMap = new HashMap<>();
        for (int i = 0; i < filterItems.size(); i++) {
            ItemStack stack = filterItems.get(i);
            if (!stack.isEmpty()) {
                filterItemsMap.put(i, stack);
            }
        }

        PacketDistributor.sendToServer(new SyncFilterItemsPacketC2S(filterItemsMap, this.menu.getBlockEntity().getBlockPos()));
    }


    public List<ItemStack> getAllItems() {
        return menu.itemStacks;
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

        this.filterItems = getMenu().blockEntity.getImportItems();

        while (filterItems.size() < 9) {
            filterItems.add(ItemStack.EMPTY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        Buttons.DEFAULT_INTERFACE.blit(guiGraphics, leftPos, topPos);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int startX = leftPos + 8;

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
            Buttons.DEFAULT_INTERFACE.renderButtonTooltips(guiGraphics, this.font, mouseX, mouseY, leftPos, topPos);
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

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MousePositionManagerUtil.getLastKnownPosition();

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
                } else {
                    filterItems.set(i, ItemStack.EMPTY);
                    selectedFilter = ItemStack.EMPTY;
                }

                syncFilterItems();

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

        Buttons.DEFAULT_INTERFACE.mouseClicked( (int) mouseX, (int) mouseY, button, leftPos, topPos);

        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
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
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
