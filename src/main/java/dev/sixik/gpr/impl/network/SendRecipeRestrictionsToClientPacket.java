package dev.sixik.gpr.impl.network;

import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.impl.events.GPRClientEvents;
import dev.sixik.gpr.impl.registry.RecipeRestrictionData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SendRecipeRestrictionsToClientPacket(List<RecipeRestrictionData> restrictions) implements CustomPacketPayload {

    public static final Type<SendRecipeRestrictionsToClientPacket> TYPE =
            new Type<>(ResourceLocation.tryBuild(GameProgressionRecipes.MODID, "recipe_restrictions_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SendRecipeRestrictionsToClientPacket> STREAM_CODEC =
            StreamCodec.of(SendRecipeRestrictionsToClientPacket::encode, SendRecipeRestrictionsToClientPacket::decode);

    private static SendRecipeRestrictionsToClientPacket decode(RegistryFriendlyByteBuf buffer) {
        int restrictionCount = buffer.readVarInt();
        List<RecipeRestrictionData> restrictions = new ArrayList<>(restrictionCount);

        for (int i = 0; i < restrictionCount; i++) {
            byte registerType = buffer.readByte();
            short stage = buffer.readShort();
            int blockId = buffer.readVarInt();
            int recipeDataLength = buffer.readVarInt();
            int[] recipeData = new int[recipeDataLength];

            for (int j = 0; j < recipeDataLength; j++) {
                recipeData[j] = buffer.readVarInt();
            }

            restrictions.add(new RecipeRestrictionData(registerType, stage, blockId, recipeData));
        }

        return new SendRecipeRestrictionsToClientPacket(List.copyOf(restrictions));
    }

    private static void encode(RegistryFriendlyByteBuf buffer, SendRecipeRestrictionsToClientPacket packet) {
        buffer.writeVarInt(packet.restrictions.size());

        for (RecipeRestrictionData restriction : packet.restrictions) {
            buffer.writeByte(restriction.registerType());
            buffer.writeShort(restriction.stage());
            buffer.writeVarInt(restriction.blockId());
            buffer.writeVarInt(restriction.recipeData().length);

            for (int value : restriction.recipeData()) {
                buffer.writeVarInt(value);
            }
        }
    }

    public static void handle(SendRecipeRestrictionsToClientPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> GPRClientEvents.onRestrictionsSyncEvent(packet.restrictions));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
