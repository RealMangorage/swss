package org.mangorage.swiss.network;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.StorageNetworkManager;

public record CreateNetworkPacketC2S(String networkName, String password) implements CustomPacketPayload {
    public static final Type<CreateNetworkPacketC2S> TYPE = new Type<>(SWISS.modRL("create_network"));

    public static final IPayloadHandler<CreateNetworkPacketC2S> HANDLER = (packet, context) -> {
        final var player = context.player();

        StorageNetworkManager.getInstance().createNetwork(packet.networkName(), packet.password(), (ServerPlayer) player);
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateNetworkPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CreateNetworkPacketC2S::networkName,
            ByteBufCodecs.STRING_UTF8, CreateNetworkPacketC2S::password,
            CreateNetworkPacketC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}