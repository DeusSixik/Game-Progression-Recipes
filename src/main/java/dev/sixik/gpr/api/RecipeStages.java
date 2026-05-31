package dev.sixik.gpr.api;

import dev.sixik.gpf.api.Stages;
import dev.sixik.gpr.api.restriction_collector.RecipeRestrictionRawData;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Arrays;

public final class RecipeStages {

    public static void addRecipe(String stage, RecipeType<?> recipeType, String[] recipes) {
        addRecipe(stage, recipeType,
                Arrays.stream(recipes).map(ResourceLocation::tryParse).toArray(ResourceLocation[]::new)
        );
    }

    public static void addRecipe(String stage, RecipeType<?> recipeType, ResourceLocation[] recipes) {
        GPRRegistry.INSTANCE.addPendingData(
                new RecipeRestrictionRawData(
                        RecipeRegisterType.BY_RECIPE_TYPE,
                        stage,
                        null,
                        recipeType,
                        recipes
                )
        );
    }
}
