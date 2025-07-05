package org.mangorage.swiss.storage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum Permission {
    CAN_EXTRACT,
    CAN_INSERT,
    CAN_ACCESS,
    ADMIN, // Can access everything, except affect an owner.
    OWNER; // Can access everything

    public static final IntFunction<Permission> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<ByteBuf, Permission> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
}
