package org.mangorage.swiss.screen.storagepanel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.mangorage.swiss.screen.MSMenuTypes;
import org.mangorage.swiss.screen.util.Interact;
import org.mangorage.swiss.storage.network.ISyncableNetworkHandler;
import org.mangorage.swiss.network.SyncNetworkItemsPacketS2C;
import org.mangorage.swiss.registry.SWISSBlocks;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.network.Permission;
import org.mangorage.swiss.storage.util.IPacketRequest;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;

import java.util.List;
import java.util.Set;

public final class StoragePanelMenu extends AbstractContainerMenu implements ISyncableNetworkHandler, IPacketRequest, Interact {

    private StorageItemPanelBlockEntity blockEntity;
    List<ItemStack> itemStacks = List.of();
    private Level level;
    private ContainerData data;
    private Player player;
    private BlockPos blockPos;

    public StoragePanelMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(1));

    }


    public StoragePanelMenu(int containerID, Inventory inventory, BlockPos blockPos, ContainerData data) {
        super(MSMenuTypes.STORAGE_MENU.get(), containerID);
        this.player = inventory.player;
        this.blockPos = blockPos;
        this.level = inventory.player.level();
        this.data = data;
        this.blockEntity = (StorageItemPanelBlockEntity) this.level.getBlockEntity(blockPos);

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);

    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (!level.isClientSide()) {
            final var items = blockEntity.getItems();
            final var sp = (ServerPlayer) player;
            sp.connection.send(new SyncNetworkItemsPacketS2C(items));
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
    public boolean clickMenuButton(Player player, int id) {
        return super.clickMenuButton(player, id);
    }




    //Todo : make shift clicking out of the network work
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack originalStack = sourceStack.copy();

        if (!playerIn.level().isClientSide()) {
            // Shift-clicked in player inventory: try to insert into network
            if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
                var insertHandler = ItemHandlerLookup.getLookupForInsert(blockEntity.getNetwork());
                ItemStack leftover = insertHandler.insertIntoHandlers(sourceStack);

                if (leftover.isEmpty()) {
                    sourceSlot.set(ItemStack.EMPTY);
                } else {
                    sourceSlot.set(leftover);
                }
                sourceSlot.setChanged();
                return ItemStack.EMPTY;
            }
            // Shift-clicked in TE (network) slots: try to extract full stack and put into player inventory
            else if (index >= TE_INVENTORY_FIRST_SLOT_INDEX && index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
                // Directly try to move the item stack from the tile entity slot into the player inventory slots
                if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                    // Failed to move to player inventory
                    return ItemStack.EMPTY;
                }

                // After moving, if all items were moved, clear the source slot
                if (sourceStack.isEmpty()) {
                    sourceSlot.set(ItemStack.EMPTY);
                } else {
                    sourceSlot.setChanged();
                }

                return originalStack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos), player, SWISSBlocks.STORAGE_ITEM_PANEL_BLOCK.get());
    }

    public void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 36 + (StoragePanelScreen.visibleRows * 18) + i * 18));
            }
        }
    }

    public void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 94  + (StoragePanelScreen.visibleRows * 18) ));
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
        final var items = blockEntity.getItems();
        player.connection.send(new SyncNetworkItemsPacketS2C(items));
    }

    boolean hasPermission(Network network, Set<Permission> permissions) {
        return network.hasPermission(player.getUUID(), permissions);
    }

    @Override
    public void clicked(ClickType clickType, ItemStack itemStack) {
        if (clickType == ClickType.PICKUP) {
            if (getCarried().isEmpty() && itemStack != null) {
                // Check Permissions
                if (!hasPermission(blockEntity.getNetwork(), Set.of(Permission.OWNER, Permission.ADMIN, Permission.CAN_EXTRACT))) {
                    player.sendSystemMessage(Component.literal("No Permission to extract from storage!"));
                    return;
                }
                final var lookup = ItemHandlerLookup.getLookupForExtract(blockEntity.getNetwork());
                final var result = lookup.findAny(itemStack.getItem(), Math.min(itemStack.getCount(), itemStack.getMaxStackSize()));
                if (!result.isEmpty()) {
                    setCarried(result);
                }

            } else if (!getCarried().isEmpty()) {
                if (!hasPermission(blockEntity.getNetwork(), Set.of(Permission.OWNER, Permission.ADMIN, Permission.CAN_INSERT))) {
                    player.sendSystemMessage(Component.literal("No Permission to insert into storage!"));
                    return;
                }
                final var stack = getCarried();
                final var lookup = ItemHandlerLookup.getLookupForInsert(blockEntity.getNetwork());
                final var remainder = lookup.insertIntoHandlers(stack);
                if (remainder.isEmpty()) {
                    setCarried(ItemStack.EMPTY);
                } else {
                    setCarried(remainder);
                }
            }
        }
    }

    public record ItemList(List<ItemStack> stacks) {}
}
