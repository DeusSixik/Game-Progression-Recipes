package dev.sixik.gpr.impl.compat.kubejs;

import dev.sixik.gpr.api.RecipeStages;
import net.minecraft.resources.ResourceLocation;

public final class GPRKubeJS {

    private GPRKubeJS() {
    }

    public static void addRecipe(String stage, String recipeType, String[] recipes) {
        RecipeStages.addRecipe(stage, recipeType, recipes);
    }

    public static void addRecipeResources(String stage, String recipeType, ResourceLocation[] recipes) {
        RecipeStages.addRecipe(stage, recipeType, recipes);
    }
}
