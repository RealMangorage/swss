package org.mangorage.swiss.storage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.UUID;

/**
 * Tells each player the NetworkInfo and if they are in the network...
 **/
public record NetworkInfo(String networkName, UUID networkId, boolean isJoined) {
    public static final StreamCodec<ByteBuf, NetworkInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, NetworkInfo::networkName,
            UUIDUtil.STREAM_CODEC, NetworkInfo::networkId,
            ByteBufCodecs.BOOL, NetworkInfo::isJoined,
            NetworkInfo::new
    );

    public static final StreamCodec<ByteBuf, List<NetworkInfo>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );
}
