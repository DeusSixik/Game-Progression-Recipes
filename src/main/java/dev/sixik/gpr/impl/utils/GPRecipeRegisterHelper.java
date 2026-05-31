package dev.sixik.gpr.impl.utils;

import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

public class GPRecipeRegisterHelper {

    private static RecipeManager recipeManager;

    public static void setRecipeManager(RecipeManager manager) {
        recipeManager = manager;
    }

    @NotNull
    public static RecipeManager getRecipeManager() {
        if(recipeManager == null) {
            throw new IllegalStateException("RecipeManager is not set!");
        }

        return recipeManager;
    }
}
