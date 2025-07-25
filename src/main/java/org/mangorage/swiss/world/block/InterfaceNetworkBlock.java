package org.mangorage.swiss.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

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

    public static final BooleanProperty CONNECTED = BlockStateProperties.ENABLED;

    public InterfaceNetworkBlock(Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(properties, function);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_MAP.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(CONNECTED);
    }
}
