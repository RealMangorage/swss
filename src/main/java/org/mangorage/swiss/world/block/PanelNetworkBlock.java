package org.mangorage.swiss.world.block;

import net.minecraft.Util;
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

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class PanelNetworkBlock extends AbstractBaseNetworkBlock {

    private static final Map<Direction, VoxelShape> SHAPE_CACHE = Util.make(new EnumMap<>(Direction.class), map -> {
        double inset = 1.0;          // 1 pixel inset on each side (16 - 2 = 14)
        double extent = 15.0;        // 16 - 1 = 15
        double thickness = 2.0;      // 2 pixels thick

        map.put(Direction.NORTH, Block.box(inset, inset, 16.0 - thickness, extent, extent, 16.0));
        map.put(Direction.SOUTH, Block.box(inset, inset, 0.0, extent, extent, thickness));
        map.put(Direction.WEST,  Block.box(16.0 - thickness, inset, inset, 16.0, extent, extent));
        map.put(Direction.EAST,  Block.box(0.0, inset, inset, thickness, extent, extent));
        map.put(Direction.UP,    Block.box(inset, 0.0, inset, extent, thickness, extent));
        map.put(Direction.DOWN,  Block.box(inset, 16.0 - thickness, inset, extent, 16.0, extent));
    });

    public PanelNetworkBlock(Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(properties, function);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_CACHE.get(state.getValue(FACING));
    }
}
