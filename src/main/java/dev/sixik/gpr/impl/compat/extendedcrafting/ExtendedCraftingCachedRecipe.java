package dev.sixik.gpr.impl.compat.extendedcrafting;

import com.blakebr0.cucumber.inventory.CachedRecipe;
import dev.sixik.gpr.impl.mixin.recipes.mods.extended_crafting.CachedRecipeAccessor;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ExtendedCraftingCachedRecipe<I extends RecipeInput, T extends Recipe<I>> extends CachedRecipe<I, T> {

    private final BlockEntity holder;
    private final RecipeType<T> type;

    public ExtendedCraftingCachedRecipe(BlockEntity holder, RecipeType<T> type) {
        super(type);
        this.holder = holder;
        this.type = type;
    }

    @Override
    public boolean check(I inventory, Level level) {
        if (this.get() != null && this.get().matches(inventory, level)) {
            return true;
        } else {
            T recipe = level.getRecipeManager().getRecipeFor(this.type, inventory, level).map(RecipeHolder::value).orElse(null);
            if(RecipeStageUtils.isUnlocked(holder, recipe)) {
                ((CachedRecipeAccessor<I,T>)this).setRecipe(recipe);
            } else {
                ((CachedRecipeAccessor<I,T>)this).setRecipe(null);
            }

            return this.get() != null;
        }
    }
}
