package org.mangorage.swiss.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;
import org.mangorage.swiss.world.block.entity.TickingBlockEntity;

import java.util.function.BiFunction;

public final class StoragePanelBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, BlockEntity> function;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public StoragePanelBlock(Properties p_49795_, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(p_49795_);
        this.function = function;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        double inset = 1.0;          // 1 pixel inset on each side (16 - 2 = 14)
        double extent = 15.0;        // 16 - 1 = 15
        double thickness = 2.0;      // 2 pixels thick

        return switch (facing) {
            case NORTH -> Block.box(inset, inset, 16.0 - thickness, extent, extent, 16.0);
            case SOUTH -> Block.box(inset, inset, 0.0, extent, extent, thickness);
            case WEST  -> Block.box(16.0 - thickness, inset, inset, 16.0, extent, extent);
            case EAST  -> Block.box(0.0, inset, inset, thickness, extent, extent);
            case UP    -> Block.box(inset, 0.0, inset, extent, thickness, extent);
            case DOWN  -> Block.box(inset, 16.0 - thickness, inset, extent, 16.0, extent);
        };
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        Direction direction = context.getNearestLookingDirection();

        if (blockState.is(Blocks.WATER)) {
            return this.defaultBlockState().setValue(WATERLOGGED, true).setValue(FACING, direction.getOpposite());
        } else {
            return this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, direction.getOpposite());
        }
    }

    /* ROTATION */
    @Override
    public @NotNull BlockState rotate(BlockState blockState, @NotNull LevelAccessor level, @NotNull BlockPos blockPos, Rotation direction) {
        return blockState.setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)).setValue(FACING, direction.rotate(blockState.getValue(FACING)));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, FACING);
    }

    public @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (function == null) return null;
        return function.apply(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult hit) {

        if (!level.isClientSide()) {

            StorageItemPanelBlockEntity storageItemPanelBlockEntity = (StorageItemPanelBlockEntity) level.getBlockEntity(blockPos);
            //MENU OPEN//
            if (storageItemPanelBlockEntity instanceof StorageItemPanelBlockEntity) {
                ContainerData data = storageItemPanelBlockEntity.data;
                player.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new StoragePanelMenu(windowId, playerInventory, blockPos, data),
                        Component.literal("TEST")), (buf -> buf.writeBlockPos(blockPos)));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
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
