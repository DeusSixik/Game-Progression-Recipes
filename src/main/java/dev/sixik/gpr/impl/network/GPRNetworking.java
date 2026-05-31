package dev.sixik.gpr.impl.network;

import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.stream.Stream;

public final class GPRNetworking {

    private GPRNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GameProgressionRecipes.MODID);
        registrar.playToClient(SendRecipeRestrictionsToClientPacket.TYPE, SendRecipeRestrictionsToClientPacket.STREAM_CODEC, SendRecipeRestrictionsToClientPacket::handle);
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        sendRestrictions(event.getRelevantPlayers());
    }

    public static void syncRestrictionsToAllPlayers() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        sendRestrictions(server.getPlayerList().getPlayers());
    }

    private static void sendRestrictions(Iterable<? extends net.minecraft.server.level.ServerPlayer> players) {
        SendRecipeRestrictionsToClientPacket payload =
                new SendRecipeRestrictionsToClientPacket(GPRRegistry.INSTANCE.createRestrictionSnapshot());
        players.forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    private static void sendRestrictions(Stream<? extends net.minecraft.server.level.ServerPlayer> players) {
        SendRecipeRestrictionsToClientPacket payload =
                new SendRecipeRestrictionsToClientPacket(GPRRegistry.INSTANCE.createRestrictionSnapshot());
        players.forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }
}
