package dev.sixik.gpr.impl.utils;

import dev.sixik.gpf.api.Stages;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.api.RecipeIndex;
import dev.sixik.gpr.api.RecipeTypeSupport;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;

public class RecipeStageUtils {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static UUID getPlayerId(Player player) {
        return player.getGameProfile().getId();
    }

    public static boolean hasRestriction(RecipeType<?> recipeType) {
        return RecipeTypeSupport.get(recipeType).gpr$hasRestrictions();
    }

    public static List<RecipeHolder<? extends Recipe<?>>> filterRecipes(
            List<RecipeHolder<? extends Recipe<?>>> inputRecipes, UUID player,
            byte predicateType
    ) {
        ObjectArrayList<RecipeHolder<? extends Recipe<?>>> outputRecipes = new ObjectArrayList<>();
        switch (predicateType) {
            case 0: {
                for (int i = 0; i < inputRecipes.size(); i++) {
                    RecipeHolder<?> recipe = inputRecipes.get(i);
                    if (isUnlocked(player, recipe)) {
                        outputRecipes.add(recipe);
                    }
                }
            }
        }

        return outputRecipes;
    }

    public static boolean isUnlocked(BlockEntity block, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        if (block == null || recipeHolder == null) {
            return false;
        }

        RecipeType<?> recipeType = recipeHolder.value().getType();
        if (!hasRestriction(recipeType)) {
            return true;
        }

        int recipeId = RecipeIndex.get(recipeHolder).gpr$getIndex();
        if (recipeId == -1) {
            return true;
        }

        short[] requiredStages = GPRRegistry.INSTANCE.getRequiredStages(recipeId, block.getType(), recipeType);
        if (requiredStages.length == 0) {
            return true;
        }

        UUID ownerId = BlockEntityOwner.get(block).gpr$getOwner();
        return ownerId != null && isOwnerHaveStage(ownerId, requiredStages);
    }

    public static boolean isUnlocked(UUID player, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        if (recipeHolder == null) {
            return false;
        }

        RecipeType<?> recipeType = recipeHolder.value().getType();
        if (!hasRestriction(recipeType)) {
            return true;
        }

        int recipeId = RecipeIndex.get(recipeHolder).gpr$getIndex();
        if (recipeId == -1) {
            return true;
        }

        short[] requiredStages = GPRRegistry.INSTANCE.getRequiredStages(recipeId, null, recipeType);
        if (requiredStages.length == 0) {
            return true;
        }

        return player != null && isOwnerHaveStage(player, requiredStages);
    }

    public static boolean isOwnerHaveStage(BlockEntity block, short stageId) {
        return isOwnerHaveStage(BlockEntityOwner.get(block).gpr$getOwner(), stageId);
    }

    public static boolean isOwnerHaveStage(BlockEntity block, short[] stageId) {
        return isOwnerHaveStage(BlockEntityOwner.get(block).gpr$getOwner(), stageId);
    }

    public static boolean isOwnerHaveStage(UUID playerId, short stageId) {
        return Stages.hasStage(playerId, stageId);
    }

    public static boolean isOwnerHaveStage(UUID playerId, short[] stageId) {
        return Stages.hasStages(playerId, stageId);
    }
}
