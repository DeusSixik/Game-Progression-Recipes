package dev.sixik.gpr.impl.mixin.recipes.mods.extended_crafting;

import com.blakebr0.cucumber.inventory.CachedRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CachedRecipe.class)
public interface CachedRecipeAccessor<I extends RecipeInput, T extends Recipe<I>> {

    @Accessor
    void setRecipe(T recipe);
}
