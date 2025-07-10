package org.mangorage.swiss.screen.exporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.network.SyncFilterItemsPacketS2C;
import org.mangorage.swiss.network.SyncNetworkItemsPacketS2C;
import org.mangorage.swiss.registry.SWISSBlocks;
import org.mangorage.swiss.screen.FilterMenu;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.config_block.ConfigureBlockNetworkMenu;
import org.mangorage.swiss.screen.setting.SettingsMenu;
import org.mangorage.swiss.screen.util.Interact;
import org.mangorage.swiss.storage.network.ISyncableNetworkHandler;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.storage.util.IPacketRequest;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemExporterBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExporterMenu extends AbstractContainerMenu implements ISyncableNetworkHandler, IPacketRequest, Interact, FilterMenu {

    public ItemExporterBlockEntity blockEntity;
    List<ItemStack> itemStacks = List.of();
    public Level level;
    private ContainerData data;
    public Player player;
    private BlockPos blockPos;
    public List<ItemStack> filterItems = new ArrayList<>();


    public ExporterMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(1));

    }

    public ExporterMenu(int containerID, Inventory inventory, BlockPos blockPos, ContainerData data) {
        super(MSMenuTypes.EXPORTER_MENU.get(), containerID);
        this.player = inventory.player;
        this.blockPos = blockPos;
        this.level = inventory.player.level();
        this.data = data;
        this.blockEntity = (ItemExporterBlockEntity) this.level.getBlockEntity(blockPos);

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);

        if (!level.isClientSide()) {

            Map<Integer, ItemStack> filterMap = new HashMap<>();
            List<ItemStack> items = blockEntity.getExportItems();
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).isEmpty()) {
                    filterMap.put(i, items.get(i));
                }
            }

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFilterItemsPacketS2C(filterMap, blockPos));
        }

    }

    public ItemExporterBlockEntity getBlockEntity() {
        return (ItemExporterBlockEntity) blockEntity;
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (!level.isClientSide()) {
            //final var items = blockEntity.getItems();
            //final var sp = (ServerPlayer) player;
            //sp.connection.send(new SyncNetworkItemsPacketS2C(items));
        }
    }

    @Override
    public void clicked(ItemStack itemStack, CompoundTag extraData, ClickType clickType, int button) {
        if (button == 1) {
            player.openMenu(
                    new SimpleMenuProvider(
                            (windowId, playerInventory, playerEntity) -> new ConfigureBlockNetworkMenu(windowId, playerInventory, blockPos, data),
                            Component.translatable("gui.swiss.configure_block_network")
                    ), buf -> {
                        buf.writeBlockPos(blockPos);
                        NetworkInfo.LIST_STREAM_CODEC.encode(buf, StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player));
                    }
            );

        }
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos),
                player, SWISSBlocks.EXPORTER_ITEM_INTERFACE_BLOCK.get());
    }


    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18  + 17, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18  + 17, 142));
        }
    }

    @Override
    public void sync(Object object) {
        if (object instanceof ItemList(List<ItemStack> stacks)) {
            this.itemStacks = stacks;
        }
    }

    @Override
    public void requested(ServerPlayer player) {

    }

    @Override
    public List<ItemStack> getFilterItems() {
        return filterItems;
    }

    @Override
    public void setFilterItems(List<ItemStack> items) {
        this.filterItems = items;
    }


    public record ItemList(List<ItemStack> stacks) {}
}
