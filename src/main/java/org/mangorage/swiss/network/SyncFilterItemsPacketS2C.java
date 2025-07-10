package org.mangorage.swiss.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.FilterMenu;
import org.mangorage.swiss.screen.exporter.ExporterMenu;
import org.mangorage.swiss.screen.importer.ImporterMenu;

import java.util.*;

public record SyncFilterItemsPacketS2C(Map<Integer, ItemStack> stackBySlot, BlockPos entityPos) implements CustomPacketPayload {

    public static final Type<SyncFilterItemsPacketS2C> TYPE = new Type<>(SWISS.modRL("sync_filter_items_s2c"));

    public static final IPayloadHandler<SyncFilterItemsPacketS2C> HANDLER = (pkt, ctx) -> {

        ExporterMenu menu = getCurrentExporterMenu();
        if (menu != null) {
            menu.filterItems = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));

            for (Map.Entry<Integer, ItemStack> entry : pkt.stackBySlot.entrySet()) {
                int slot = entry.getKey();
                if (slot < 9) {
                    menu.filterItems.set(entry.getKey(), entry.getValue());
                }
            }
        }

    };


    private static ExporterMenu getCurrentExporterMenu() {
        if(Minecraft.getInstance().player != null) {
            var container = Minecraft.getInstance().player.containerMenu;
            if (container instanceof ExporterMenu exporterMenu) {
                return exporterMenu;
            }
        }

        return null;
    }


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
