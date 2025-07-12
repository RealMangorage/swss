package org.mangorage.swiss.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.config.ClientConfig;

public record SyncVisibleRowsC2S(int visibleRows) implements CustomPacketPayload {

    public static final Type<SyncVisibleRowsC2S> TYPE = new Type<>(SWISS.modRL("sync_visible_rows_c2s"));


    public static final IPayloadHandler<SyncVisibleRowsC2S> HANDLER = (pkt, ctx) -> {

        ServerPlayer player = (ServerPlayer) ctx.player();
        int newRows = pkt.visibleRows();

        player.getPersistentData().putInt("swiss_visible_rows", newRows);

    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncVisibleRowsC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncVisibleRowsC2S::visibleRows,
            SyncVisibleRowsC2S::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
