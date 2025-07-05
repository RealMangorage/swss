package org.mangorage.swiss.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class Packets {
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1.0.0")
                .playToClient(SyncNetworkItemsPacketS2C.TYPE, SyncNetworkItemsPacketS2C.STREAM_CODEC, SyncNetworkItemsPacketS2C.HANDLER);
    }
}
