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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
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

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, Player player
    ) {
        return filterRecipes(original, getPlayerId(player), 0);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, BlockEntity block
    ) {
        return filterRecipes(original, BlockEntityOwner.get(block).gpr$getOwner(), 0);
    }

    public static List<RecipeHolder<? extends Recipe<?>>> filterRecipes(
            List<RecipeHolder<? extends Recipe<?>>> inputRecipes, Player player
    ) {
        return filterRecipes(inputRecipes, getPlayerId(player), 0);
    }

    public static List<RecipeHolder<? extends Recipe<?>>> filterRecipes(
            List<RecipeHolder<? extends Recipe<?>>> inputRecipes, UUID player
    ) {
        return filterRecipes(inputRecipes, player, 0);
    }

    public static List<RecipeHolder<? extends Recipe<?>>> filterRecipes(
            List<RecipeHolder<? extends Recipe<?>>> inputRecipes, UUID player,
            int predicateType
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

    public static <T extends Recipe<?>> List<T> filterRecipesValue(
            List<T> inputRecipes, UUID player,
            int predicateType
    ) {
        ObjectArrayList<T> outputRecipes = new ObjectArrayList<>();
        switch (predicateType) {
            case 0: {
                for (int i = 0; i < inputRecipes.size(); i++) {
                    T recipe = inputRecipes.get(i);
                    if (isUnlocked(player, recipe)) {
                        outputRecipes.add(recipe);
                    }
                }
            }
        }

        return outputRecipes;
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, UUID player
    ) {
        return filterRecipes(original, player, 0);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, UUID player, int predicateType
    ) {
        if(original == null) return null;

        final int size = original.size();
        if (size == 0) return Collections.emptyList();

        final List<RecipeHolder<T>> result = new ObjectArrayList<>(size);
        switch (predicateType) {
            case 0: {
                for (final RecipeHolder<T> holder : original) {
                    if (isUnlocked(player, holder)) {
                        result.add(holder);
                    }
                }
            }
        }

        return result;
    }

    public static boolean isUnlocked(BlockEntity block, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        return isUnlocked(block, recipeHolder.value());
    }

    public static boolean isUnlocked(BlockEntity block, Recipe<?> recipeHolder) {
        if (block == null || recipeHolder == null) {
            return false;
        }

        RecipeType<?> recipeType = recipeHolder.getType();
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

    public static boolean isUnlocked(Player player, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        return isUnlocked(getPlayerId(player), recipeHolder);
    }

    public static boolean isUnlocked(UUID player, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        if(recipeHolder == null) return false;

        return isUnlocked(player, recipeHolder.value());
    }

    public static boolean isUnlocked(UUID player, Recipe<?> recipeHolder) {
        if (recipeHolder == null) {
            return false;
        }

        RecipeType<?> recipeType = recipeHolder.getType();
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

    @Nullable
    public static <T extends RecipeHolder<? extends Recipe<?>>> T ifCanProcessReturnRecipeOrNull(BlockEntity blockEntity, T recipe) {
        return isUnlocked(blockEntity, recipe) ? recipe : null;
    }

}
