package org.mangorage.swiss.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class Packets {
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1.0.0")
                .playToClient(SyncNetworkItemsPacketS2C.TYPE, SyncNetworkItemsPacketS2C.STREAM_CODEC, SyncNetworkItemsPacketS2C.HANDLER)
                .playToServer(RequestNetworkItemsPacketC2S.TYPE, RequestNetworkItemsPacketC2S.STREAM_CODEC, RequestNetworkItemsPacketC2S.HANDLER)
                .playToServer(MenuInteractPacketC2S.TYPE, MenuInteractPacketC2S.STREAM_CODEC, MenuInteractPacketC2S.HANDLER);
    }
}
