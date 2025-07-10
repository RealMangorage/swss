package org.mangorage.swiss.world.block.entity.item.panels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.screen.util.HasMenu;
import org.mangorage.swiss.storage.device.DeviceType;
import org.mangorage.swiss.storage.device.ItemDevice;
import org.mangorage.swiss.registry.SWISSBlockEntities;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StorageItemPanelBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, ItemDevice, HasMenu {

    private int ticks = 0;

    public StorageItemPanelBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.STORAGE_ITEM_PANEL_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tickServer() {
        ticks++;
        if (ticks % 20 == 0) {
            connectToNetwork(); // Connect if we havent already done so...
        }
    }

    @Override
    public IItemHandler getItemHandler() {
        return null;
    }

    @Override
    public boolean canInsert(DeviceType type) {
        return false;
    }

    @Override
    public boolean canExtract(DeviceType type) {
        return false;
    }

    @Override
    public void openMenu(Player player) {
        player.openMenu(new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new StoragePanelMenu(windowId, playerInventory, getBlockPos()),
                Component.literal("Item Panel")), (buf -> buf.writeBlockPos(getBlockPos())));
    }
}
