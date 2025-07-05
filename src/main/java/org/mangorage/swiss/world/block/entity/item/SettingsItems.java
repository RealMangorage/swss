package org.mangorage.swiss.world.block.entity.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mangorage.swiss.screen.setting.SettingsMenu;
import org.mangorage.swiss.screen.test.TestMenu;

public class SettingsItems extends Item {
    public SettingsItems(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        BlockPos blockPos = player.blockPosition();
        ContainerData data = new SimpleContainerData(1);

        player.openMenu(new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new SettingsMenu(windowId, playerInventory, blockPos, data),
                Component.literal("TEST")), (buf -> buf.writeBlockPos(blockPos)));

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

}
