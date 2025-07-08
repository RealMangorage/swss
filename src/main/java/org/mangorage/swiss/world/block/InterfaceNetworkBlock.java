package org.mangorage.swiss.world.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.screen.config_block.ConfigureBlockNetworkMenu;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.storage.network.NetworkInfo;
import org.mangorage.swiss.world.block.entity.base.BaseStorageBlockEntity;
import org.mangorage.swiss.world.block.entity.item.panels.StorageItemPanelBlockEntity;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class InterfaceNetworkBlock extends AbstractBaseNetworkBlock {

    private static final Map<Direction, VoxelShape> SHAPE_MAP = new EnumMap<>(Direction.class);

    static {
        SHAPE_MAP.put(Direction.EAST, buildShape(Direction.EAST));
        SHAPE_MAP.put(Direction.WEST, buildShape(Direction.WEST));

        SHAPE_MAP.put(Direction.NORTH, buildShape(Direction.SOUTH));
        SHAPE_MAP.put(Direction.SOUTH, buildShape(Direction.NORTH));

        SHAPE_MAP.put(Direction.UP, buildShape(Direction.UP));
        SHAPE_MAP.put(Direction.DOWN, buildShape(Direction.DOWN));
    }

    private static VoxelShape buildShape(Direction direction) {
        VoxelShape shape = Shapes.empty();

        // Manually add each element from the Blockbench model
        // Convert Blockbench's [from, to] to Minecraft's [0.0, 1.0]
        shape = Shapes.or(shape, Shapes.box(5/16.0, 5/16.0, 13.5/16.0, 11/16.0, 11/16.0, 14.5/16.0));
        shape = Shapes.or(shape, Shapes.box(5/16.0, 5/16.0, 13/16.0, 11/16.0, 11/16.0, 15/16.0));
        shape = Shapes.or(shape, Shapes.box(4/16.0, 4/16.0, 15/16.0, 12/16.0, 12/16.0, 1.0));
        shape = Shapes.or(shape, Shapes.box(9/16.0, 12/16.0, 15.99/16.0, 10/16.0, 13/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(6/16.0, 12/16.0, 15.99/16.0, 7/16.0, 13/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(6/16.0, 3/16.0, 15.99/16.0, 7/16.0, 4/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(9/16.0, 3/16.0, 15.99/16.0, 10/16.0, 4/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(3/16.0, 9/16.0, 15.99/16.0, 4/16.0, 10/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(3/16.0, 6/16.0, 15.99/16.0, 4/16.0, 7/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(12/16.0, 9/16.0, 15.99/16.0, 13/16.0, 10/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(12/16.0, 6/16.0, 15.99/16.0, 13/16.0, 7/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(7/16.0, 7/16.0, 12/16.0, 9/16.0, 9/16.0, 13/16.0));
        shape = Shapes.or(shape, Shapes.box(5/16.0, 8/16.0, 10/16.0, 11/16.0, 8/16.0, 12/16.0));
        shape = Shapes.or(shape, Shapes.box(8/16.0, 5/16.0, 10/16.0, 8/16.0, 11/16.0, 12/16.0));

        // Rotate shape according to direction
        return rotateShape(Direction.SOUTH, direction, shape);
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        if (from == to) return shape;

        VoxelShape rotated = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;

            AABB newBox;

            switch (to) {
                case NORTH -> newBox = new AABB(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ);
                case EAST -> newBox = new AABB(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
                case WEST -> newBox = new AABB(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);

                case UP -> newBox = new AABB(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY); // Z -> Y
                case DOWN -> newBox = new AABB(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY); // Y -> Z

                default -> newBox = box; // SOUTH
            }

            rotated = Shapes.or(rotated, Shapes.box(
                    newBox.minX, newBox.minY, newBox.minZ,
                    newBox.maxX, newBox.maxY, newBox.maxZ
            ));
        }

        return rotated;
    }


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty CONNECTED = BlockStateProperties.ENABLED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public InterfaceNetworkBlock(Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(properties, function);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_MAP.get(state.getValue(FACING));
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
        pBuilder.add(WATERLOGGED, FACING, CONNECTED);
    }

    public @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!level.isClientSide()) {

            BaseStorageBlockEntity storageItemPanelBlockEntity = (BaseStorageBlockEntity) level.getBlockEntity(blockPos);
            //MENU OPEN//
            if (storageItemPanelBlockEntity instanceof BaseStorageBlockEntity) {
                ContainerData data = new ContainerData() {
                    @Override
                    public int get(int index) {
                        return 0;
                    }

                    @Override
                    public void set(int index, int value) {

                    }

                    @Override
                    public int getCount() {
                        return 0;
                    }
                };
                player.openMenu(new SimpleMenuProvider(
                                (windowId, playerInventory, playerEntity) -> new ConfigureBlockNetworkMenu(windowId, playerInventory, blockPos, data),
                                Component.literal("Configure Block")
                        ),
                        buf -> {
                            buf.writeBlockPos(blockPos);
                            final var info = StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player);
                            NetworkInfo.LIST_STREAM_CODEC.encode(buf, info);
                        }
                );
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
