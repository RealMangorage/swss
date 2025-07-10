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
import org.mangorage.swiss.world.block.entity.item.interfaces.ItemImporterBlockEntity;

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

            Map<Integer, ItemStack> newItems = pkt.stackBySlot();
            int maxSlot = newItems.keySet().stream().max(Integer::compareTo).orElse(-1);
            while (itemExporter.exportItems.size() <= maxSlot) {
                itemExporter.exportItems.add(ItemStack.EMPTY);
            }
            newItems.forEach(itemExporter.exportItems::set);
            itemExporter.setChanged();
        }

        if (blockEntity instanceof ItemImporterBlockEntity itemExporter) {
            itemExporter.importItems.clear();

            Map<Integer, ItemStack> newItems = pkt.stackBySlot();
            int maxSlot = newItems.keySet().stream().max(Integer::compareTo).orElse(-1);
            while (itemExporter.importItems.size() <= maxSlot) {
                itemExporter.importItems.add(ItemStack.EMPTY);
            }
            newItems.forEach(itemExporter.importItems::set);
            itemExporter.setChanged();
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
