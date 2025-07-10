package org.mangorage.swiss.network;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.storagepanel.StoragePanelMenu;
import org.mangorage.swiss.screen.util.Interact;
import org.mangorage.swiss.storage.network.ISyncableNetworkHandler;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemExporterBlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncFilterItemsPacketC2S(Map<Integer, ItemStack> stackBySlot, BlockPos entityPos) implements CustomPacketPayload {

    public static final Type<SyncFilterItemsPacketC2S> TYPE = new Type<>(SWISS.modRL("sync_filter_items_c2s"));

    public static final IPayloadHandler<SyncFilterItemsPacketC2S> HANDLER = (pkt, ctx) -> {
        final var player = ctx.player();
        BlockPos pos = pkt.entityPos();
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ItemExporterBlockEntity itemExporter) {
            itemExporter.exportItems.clear();

            // If pkt.stackBySlot() is Map<Integer, ItemStack>
            Map<Integer, ItemStack> newItems = pkt.stackBySlot();

            // Assuming exportItems is a List<ItemStack>, we must add in the correct order
            // Let's create a list sized to the max key + 1 and fill with empty stacks if needed
            int maxSlot = newItems.keySet().stream().max(Integer::compareTo).orElse(-1);

            // Clear and resize exportItems with empty stacks up to maxSlot
            while (itemExporter.exportItems.size() <= maxSlot) {
                itemExporter.exportItems.add(ItemStack.EMPTY);
            }

            // Now put the ItemStacks at correct positions
            newItems.forEach((slot, stack) -> {
                itemExporter.exportItems.set(slot, stack);
            });

            itemExporter.setChanged();

            System.out.println("packet " + itemExporter.exportItems);
        }
    };



    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFilterItemsPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, ItemStack.STREAM_CODEC),
            SyncFilterItemsPacketC2S::stackBySlot,
            BlockPos.STREAM_CODEC,
            SyncFilterItemsPacketC2S::entityPos,
            SyncFilterItemsPacketC2S::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
