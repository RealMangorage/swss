package org.mangorage.swiss.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.mangorage.swiss.network.request.RequestNetworkItemsPacketC2S;

public final class Packets {
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1.0.0")
                .playToClient(SyncNetworkItemsPacketS2C.TYPE, SyncNetworkItemsPacketS2C.STREAM_CODEC, SyncNetworkItemsPacketS2C.HANDLER)
                .playToServer(RequestNetworkItemsPacketC2S.TYPE, RequestNetworkItemsPacketC2S.STREAM_CODEC, RequestNetworkItemsPacketC2S.HANDLER)
                .playToServer(MenuInteractPacketC2S.TYPE, MenuInteractPacketC2S.STREAM_CODEC, MenuInteractPacketC2S.HANDLER)
                .playToServer(CreateNetworkPacketC2S.TYPE, CreateNetworkPacketC2S.STREAM_CODEC, CreateNetworkPacketC2S.HANDLER)
                .playToServer(JoinNetworkPacketC2S.TYPE, JoinNetworkPacketC2S.STREAM_CODEC, JoinNetworkPacketC2S.HANDLER);
    }
}
