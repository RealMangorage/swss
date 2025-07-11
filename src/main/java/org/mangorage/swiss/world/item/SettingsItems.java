package org.mangorage.swiss.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mangorage.swiss.StorageNetworkManager;
import org.mangorage.swiss.screen.misc.setting.SettingsMenu;
import org.mangorage.swiss.storage.network.NetworkInfo;

public class SettingsItems extends Item {
    public SettingsItems(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        BlockPos blockPos = player.blockPosition();

        player.openMenu(
                new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new SettingsMenu(windowId, playerInventory, blockPos),
                        Component.literal("TEST")
                ),
                buf -> {
                    buf.writeBlockPos(blockPos);
                    final var info = StorageNetworkManager.getInstance().getNetworkInfo((ServerPlayer) player);
                    NetworkInfo.LIST_STREAM_CODEC.encode(buf, info);
                });

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

}
