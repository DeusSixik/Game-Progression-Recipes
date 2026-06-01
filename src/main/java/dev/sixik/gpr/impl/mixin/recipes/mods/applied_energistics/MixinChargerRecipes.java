package dev.sixik.gpr.impl.mixin.recipes.mods.applied_energistics;

import appeng.blockentity.misc.ChargerRecipes;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.ChargerRecipe;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import dev.sixik.gpr.impl.utils.RecipeUnblockOwnerUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.UUID;

@Mixin(ChargerRecipes.class)
public class MixinChargerRecipes {


    /**
     * @author Sixik
     * @reason Restriction recipe
     */
    @Overwrite
    public static @Nullable ChargerRecipe findRecipe(Level level, ItemStack input) {
        final UUID ownerId = RecipeUnblockOwnerUtils.getOwner();

        for(final RecipeHolder<ChargerRecipe> holder : RecipeStageUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(AERecipeTypes.CHARGER), ownerId)) {
            if (holder.value().ingredient.test(input)) {
                return holder.value();
            }
        }

        return null;
    }
}
