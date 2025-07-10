package org.mangorage.swiss.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.mangorage.swiss.screen.util.HasMenu;
import org.mangorage.swiss.storage.device.IDevice;
import org.mangorage.swiss.storage.util.IRightClickable;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.function.BiFunction;

public abstract class AbstractBaseNetworkBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, BlockEntity> function;

    public AbstractBaseNetworkBlock(Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(properties);
        this.function = function;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (function == null) return null;
        return function.apply(blockPos, blockState);
    }

    boolean check(Level level, BlockPos pos) {
        final var placedOn = pos.relative(level.getBlockState(pos).getValue(InterfaceNetworkBlock.FACING).getOpposite());
        final var placedOnState = level.getBlockState(placedOn);

        if (!placedOnState.isSolid()) {
            return level.destroyBlock(pos, true);
        }
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        check(level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (check(level, pos)) return;
        if (placer.getType() == EntityType.PLAYER) {
            final var be = level.getBlockEntity(pos);
            if (be instanceof IDevice device)
                device.setOwner(placer.getUUID());
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState p_316362_, Level level, BlockPos blockPos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {
        if (level.isClientSide()) return ItemInteractionResult.CONSUME;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if (!stack.isEmpty() && blockEntity != null && blockEntity instanceof IRightClickable rightClickable) {
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

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!level.isClientSide()) {

            if (level.getBlockEntity(blockPos) instanceof HasMenu hasMenu) {
                hasMenu.openMenu(player);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
