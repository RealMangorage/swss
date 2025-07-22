package org.mangorage.swiss.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.mangorage.swiss.SWISS;
import org.mangorage.swiss.screen.panels.craftingpanel.CraftingPanelMenu;
import org.mangorage.swiss.storage.network.Network;
import org.mangorage.swiss.storage.util.ItemHandlerLookup;

import java.util.List;
import java.util.Optional;

public record SyncRecipePacketC2S(ResourceLocation recipeID) implements CustomPacketPayload {

    public static final Type<SyncRecipePacketC2S> TYPE = new Type<>(SWISS.modRL("sync_recipe_c2s"));


    public static final IPayloadHandler<SyncRecipePacketC2S> HANDLER = (pkt, ctx) -> {
        ServerPlayer player = (ServerPlayer) ctx.player();

        if (player.containerMenu instanceof CraftingPanelMenu menu) {
            MinecraftServer server = player.server;
            Optional<? extends RecipeHolder<?>> optional = server.getRecipeManager().byKey(pkt.recipeID);

            if (optional.isPresent() && optional.get().value() instanceof CraftingRecipe recipe) {
                if (menu.getNetworkHolder() == null || menu.getNetworkHolder().getNetwork() == null) {
                    return;
                }

                Network network = menu.getNetworkHolder().getNetwork();

                ItemHandlerLookup extractor = ItemHandlerLookup.getLookupForExtract(network);
                ItemHandlerLookup inserter = ItemHandlerLookup.getLookupForInsert(network);

                for (int i = 0; i < menu.craftMatrix.getContainerSize(); i++) {
                    ItemStack stackInSlot = menu.craftMatrix.getItem(i);
                    if (!stackInSlot.isEmpty()) {
                        ItemStack leftover = inserter.insertIntoHandlers(stackInSlot);
                        if (!leftover.isEmpty()) {
                            return;
                        }
                        menu.craftMatrix.setItem(i, ItemStack.EMPTY);
                    }
                }

                List<Ingredient> ingredients = recipe.getIngredients();
                ItemStack[] extractedStacks = new ItemStack[ingredients.size()];
                boolean canExtractAll = true;

                for (int slot = 0; slot < ingredients.size(); slot++) {
                    Ingredient ingredient = ingredients.get(slot);
                    if (ingredient.isEmpty()) {
                        extractedStacks[slot] = ItemStack.EMPTY;
                        continue;
                    }

                    ItemStack extracted = ItemStack.EMPTY;

                    for (ItemStack possibleMatch : ingredient.getItems()) {
                        extracted = extractor.findAny(possibleMatch.getItem(), 1);
                        if (!extracted.isEmpty()) {
                            break;
                        }
                    }

                    if (extracted.isEmpty()) {
                        canExtractAll = false;
                        break;
                    }

                    extractedStacks[slot] = extracted;
                }

                if (!canExtractAll) {
                    return;
                }

                for (int slot = 0; slot < extractedStacks.length; slot++) {
                    menu.craftMatrix.setItem(slot, extractedStacks[slot]);
                }

                menu.slotsChanged(menu.craftMatrix);
            }
        }

    };


    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRecipePacketC2S> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            SyncRecipePacketC2S::recipeID,
            SyncRecipePacketC2S::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
