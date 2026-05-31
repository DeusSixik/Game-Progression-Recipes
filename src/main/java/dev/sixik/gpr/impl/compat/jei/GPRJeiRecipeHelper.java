package dev.sixik.gpr.impl.compat.jei;

import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.crafting.Recipe;

public final class GPRJeiRecipeHelper {

    private GPRJeiRecipeHelper() { }

    public static boolean isSameRecipe(IRecipeCategory<?> category, Recipe<?> recipe) {
        return RecipeStageUtils.canCast(category.getRecipeType().getRecipeClass(), recipe.getClass());
    }
}
