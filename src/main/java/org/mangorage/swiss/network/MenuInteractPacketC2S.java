package org.mangorage.swiss.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.util.Interact;

public record MenuInteractPacketC2S(ItemStack itemStack, int clickType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MenuInteractPacketC2S> TYPE = new CustomPacketPayload.Type<>(SWISS.modRL("interact_menu"));


    public static final IPayloadHandler<MenuInteractPacketC2S> HANDLER = (pkt, ctx) -> {
        final var player = ctx.player();

        if (player.containerMenu instanceof Interact interact)
            interact.clicked(ClickType.values()[pkt.clickType()], pkt.itemStack());
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, MenuInteractPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, MenuInteractPacketC2S::itemStack,
            ByteBufCodecs.INT, MenuInteractPacketC2S::clickType,
            MenuInteractPacketC2S::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
