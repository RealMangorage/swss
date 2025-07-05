package org.mangorage.swiss.storage.util;

import net.minecraft.server.level.ServerPlayer;

public interface IPacketRequest {
    void requested(ServerPlayer player);
}
