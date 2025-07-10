package org.mangorage.swiss.world.block.entity.item.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.registry.SWISSBlocks;
import org.mangorage.swiss.screen.config_block.ConfigureBlockNetworkMenu;
import org.mangorage.swiss.screen.importer.ImporterMenu;
import org.mangorage.swiss.screen.util.HasMenu;
import org.mangorage.swiss.storage.device.DeviceType;
import org.mangorage.swiss.storage.device.ItemDevice;
import org.mangorage.swiss.registry.SWISSBlockEntities;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.world.block.InterfaceNetworkBlock;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

public final class ItemInterfaceBlockEntity extends BaseStorageBlockEntity implements TickingBlockEntity, ItemDevice, HasMenu {

    private int ticks = 0;

    public ItemInterfaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(SWISSBlockEntities.STORAGE_ITEM_INTERFACE_BLOCK_ENTITY.get(), pos, blockState);
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
        Direction facing = getBlockState().getValue(InterfaceNetworkBlock.FACING);
        BlockPos outputPos = getBlockPos().relative(facing.getOpposite());
        BlockState outputState = level.getBlockState(outputPos);
        BlockEntity outputBE = level.getBlockEntity(outputPos);

        if (outputState.isAir()) return null;

        if (outputState.is(Blocks.ENDER_CHEST)) {
            final var owner = getOwner();
            if (owner != null) {
                final var ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
                if (ownerPlayer != null) {
                    return new InvWrapper(ownerPlayer.getEnderChestInventory());
                }
            }
        }

        return Capabilities.ItemHandler.BLOCK.getCapability(level, outputPos, outputState, outputBE, facing);
    }

    @Override
    public boolean canInsert(DeviceType type) {
        return true;
    }

    @Override
    public boolean canExtract(DeviceType type) {
        return true;
    }

    @Override
    public void openMenu(Player player) {
        player.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new ConfigureBlockNetworkMenu(windowId, playerInventory, getBlockPos()),
                        Component.translatable("block.swiss.storage_item_interface")
                ),
                buf -> {
                    buf.writeBlockPos(getBlockPos());
                    final var info = StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player);
                    NetworkInfo.LIST_STREAM_CODEC.encode(buf, info);
                }
        );
    }
}
