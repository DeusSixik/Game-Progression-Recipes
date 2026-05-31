package dev.sixik.gpr.impl.mixin.recipes.mods.electrodynamics;

import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.type.ComponentProcessor;

import java.util.List;

@Mixin(ComponentProcessor.class)
public abstract class MixinComponentProcessor implements IComponent {

    @Shadow
    private GenericTile holder;

    @Redirect(method = "getRecipe(Lnet/minecraft/world/item/crafting/RecipeType;I)Lvoltaic/common/recipe/VoltaicRecipe;", at = @At(value = "INVOKE", target = "Lvoltaic/common/recipe/VoltaicRecipe;findRecipesbyType(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    public List<RecipeHolder<VoltaicRecipe>> gpr$redirect_1(RecipeType<? extends VoltaicRecipe> typeIn, Level world) {
        return RecipeStageUtils.filterRecipes(VoltaicRecipe.findRecipesbyType(typeIn, world), holder);
    }
}
