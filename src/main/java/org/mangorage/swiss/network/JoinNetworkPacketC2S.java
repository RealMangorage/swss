package org.mangorage.swiss.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.StorageNetworkManager;

import java.util.UUID;

public record JoinNetworkPacketC2S(UUID networkId, String password) implements CustomPacketPayload {
    public static final Type<JoinNetworkPacketC2S> TYPE = new Type<>(SWISS.modRL("join_network"));

    public static final IPayloadHandler<JoinNetworkPacketC2S> HANDLER = (packet, context) -> {
        final var player = context.player();

        StorageNetworkManager.getInstance().joinNetwork((ServerPlayer) player, packet.networkId(), packet.password());
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, JoinNetworkPacketC2S> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, JoinNetworkPacketC2S::networkId,
            ByteBufCodecs.STRING_UTF8, JoinNetworkPacketC2S::password,
            JoinNetworkPacketC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}