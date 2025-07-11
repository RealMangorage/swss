package org.mangorage.swiss.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.storage.util.IUpdatable;
import org.mangorage.swiss.storage.network.ISyncableNetworkHandler;
import org.mangorage.swiss.screen.panels.storagepanel.StoragePanelMenu;

import java.util.List;

public record SyncNetworkItemsPacketS2C(List<ItemStack> items) implements CustomPacketPayload {

    public static final Type<SyncNetworkItemsPacketS2C> TYPE = new Type<>(SWISS.modRL("sync_network_to_menu"));


    public static final IPayloadHandler<SyncNetworkItemsPacketS2C> HANDLER = (pkt, ctx) -> {
        final var player = ctx.player();

        if (player instanceof LocalPlayer lp && player.containerMenu instanceof ISyncableNetworkHandler handler) {
            handler.sync(new StoragePanelMenu.ItemList(pkt.items()));
            if (Minecraft.getInstance().screen instanceof IUpdatable iUpdatable)
                iUpdatable.update();
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncNetworkItemsPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC, SyncNetworkItemsPacketS2C::items,
            SyncNetworkItemsPacketS2C::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
