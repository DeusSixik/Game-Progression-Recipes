package dev.sixik.gpr.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public interface RecipeManagerExtern {

    static RecipeManagerExtern get(RecipeManager manager) {
        if(manager instanceof RecipeManagerExtern out)
            return out;
        throw new IllegalArgumentException("Need implement RecipeManagerExtern for " + manager.getClass().getName());
    }

    RecipeHolder[] getRecipeHoldersRaw();

    Recipe[] getRecipesRaw();

    int getRecipeIndex(ResourceLocation id);

    RecipeHolder<?> getRecipeHolderByIndex(int index);

    ResourceLocation getRecipeIdByIndex(int index);
}
