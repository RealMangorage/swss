package org.mangorage.swiss.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.storage.util.IPacketRequest;

public final class RequestNetworkItemsPacketC2S implements CustomPacketPayload {
    public static final RequestNetworkItemsPacketC2S INSTANCE = new RequestNetworkItemsPacketC2S();

    public static final CustomPacketPayload.Type<RequestNetworkItemsPacketC2S> TYPE = new CustomPacketPayload.Type<>(SWISS.modRL("request_network_items"));

    public static final IPayloadHandler<RequestNetworkItemsPacketC2S> HANDLER = (pkt, ctx) -> {
        final var player = ctx.player();
        if (player.containerMenu instanceof IPacketRequest packetRequest) {
            packetRequest.requested((ServerPlayer) player);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestNetworkItemsPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    RequestNetworkItemsPacketC2S() {}

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
