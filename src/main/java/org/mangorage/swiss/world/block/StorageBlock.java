package org.mangorage.swiss.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.storage.util.IRightClickable;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;

import java.util.function.BiFunction;

public final class StorageBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, BlockEntity> function;

    public StorageBlock(Properties p_49795_, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(p_49795_);
        this.function = function;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (function == null) return null;
        return function.apply(blockPos, blockState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState p_316362_, Level level, BlockPos blockPos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {
        if (level.isClientSide()) return ItemInteractionResult.CONSUME;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if (blockEntity != null && blockEntity instanceof IRightClickable rightClickable) {
            rightClickable.onPlayerClick(stack, player);
            return ItemInteractionResult.SUCCESS;
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity != null && blockEntity instanceof TickingBlockEntity tickingBlockEntity) {
                if (level.isClientSide()){
                    tickingBlockEntity.tickClient();
                } else {
                    tickingBlockEntity.tickServer();
                }
            }
        };
    }
}
