package org.mangorage.swiss.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemExporterBlockEntity;

import java.util.HashMap;
import java.util.Map;

public record SyncFilterItemsPacketS2C(Map<Integer, ItemStack> stackBySlot, BlockPos entityPos) implements CustomPacketPayload {

    public static final Type<SyncFilterItemsPacketS2C> TYPE = new Type<>(SWISS.modRL("sync_filter_items_s2c"));

    public static final IPayloadHandler<SyncFilterItemsPacketS2C> HANDLER = (pkt, ctx) -> {
        var player = ctx.player(); // null on client, so use ctx.player()
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockEntity blockEntity = level.getBlockEntity(pkt.entityPos());
        if (blockEntity instanceof ItemExporterBlockEntity itemExporter) {
            itemExporter.exportItems.clear();

            Map<Integer, ItemStack> newItems = pkt.stackBySlot();

            int maxSlot = newItems.keySet().stream().max(Integer::compareTo).orElse(-1);

            while (itemExporter.exportItems.size() <= maxSlot) {
                itemExporter.exportItems.add(ItemStack.EMPTY);
            }

            newItems.forEach((slot, stack) -> {
                itemExporter.exportItems.set(slot, stack);
            });

            itemExporter.setChanged();

            System.out.println("Client updated exportItems: " + itemExporter.exportItems);

        };
    };




    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFilterItemsPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, ItemStack.STREAM_CODEC),
            SyncFilterItemsPacketS2C::stackBySlot,
            BlockPos.STREAM_CODEC,
            SyncFilterItemsPacketS2C::entityPos,
            SyncFilterItemsPacketS2C::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
