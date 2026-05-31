package dev.sixik.gpr.impl.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import dev.sixik.gpr.api.RecipeStages;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.gpr.api.RecipeStage")
public class GPRCraftTweaker {

    @ZenCodeType.Method
    public static void addRecipe(String stage, IRecipeManager<Recipe<? extends RecipeInput>> recipeType, String[] recipes) {
        RecipeStages.addRecipe(stage, recipeType.getRecipeType(), recipes);
    }

    @ZenCodeType.Method
    public static void addRecipe(String stage, IRecipeManager<Recipe<? extends RecipeInput>> recipeType, ResourceLocation[] recipes) {
        RecipeStages.addRecipe(stage, recipeType.getRecipeType(), recipes);
    }

    @ZenCodeType.Method
    public static void addRecipe(String stage, String recipeType, String[] recipes) {
        RecipeStages.addRecipe(stage, resolveRecipeType(recipeType), recipes);
    }

    @ZenCodeType.Method
    public static void addRecipe(String stage, String recipeType, ResourceLocation[] recipes) {
        RecipeStages.addRecipe(stage, resolveRecipeType(recipeType), recipes);
    }

    private static net.minecraft.world.item.crafting.RecipeType<?> resolveRecipeType(String recipeType) {
        ResourceLocation recipeTypeId = ResourceLocation.tryParse(recipeType);
        if (recipeTypeId == null) {
            throw new IllegalArgumentException("Invalid recipe type id '" + recipeType + "'");
        }

        return BuiltInRegistries.RECIPE_TYPE.getOptional(recipeTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown recipe type '" + recipeType + "'"));
    }
}
