package org.mangorage.mangostorage.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
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
import org.mangorage.mangostorage.screen.InterfaceMenu;
import org.mangorage.mangostorage.world.block.entity.TestInterfaceBlockEntity;
import org.mangorage.mangostorage.world.block.entity.TickingBlockEntity;

import java.util.function.BiFunction;

public class TestInferfaceBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, BlockEntity> function;

    public TestInferfaceBlock(Properties p_49795_, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super(p_49795_);
        this.function = function;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (function == null) return null;
        return function.apply(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult hit) {

        if (!level.isClientSide()) {

            TestInterfaceBlockEntity testInterfaceBlockEntity = (TestInterfaceBlockEntity) level.getBlockEntity(blockPos);
            //MENU OPEN//
            if (testInterfaceBlockEntity instanceof TestInterfaceBlockEntity) {
                ContainerData data = testInterfaceBlockEntity.data;
                player.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new InterfaceMenu(windowId, playerInventory, blockPos, data),
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
