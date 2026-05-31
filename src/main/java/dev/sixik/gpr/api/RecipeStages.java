package dev.sixik.gpr.api;

import dev.sixik.gpr.api.restriction_collector.RecipeRestrictionRawData;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeStages {

    public static void addRecipe(String stage, RecipeType<?> recipeType, String[] recipes) {
        ResourceLocation[] parsedRecipes = new ResourceLocation[recipes.length];

        for (int i = 0; i < recipes.length; i++) {
            ResourceLocation recipeId = ResourceLocation.tryParse(recipes[i]);
            if (recipeId == null) {
                throw new IllegalArgumentException("Invalid recipe id '" + recipes[i] + "'");
            }

            parsedRecipes[i] = recipeId;
        }

        addRecipe(stage, recipeType, parsedRecipes);
    }

    public static void addRecipe(String stage, RecipeType<?> recipeType, ResourceLocation[] recipes) {
        if (recipeType == null) {
            throw new IllegalArgumentException("Recipe type must not be null");
        }

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
