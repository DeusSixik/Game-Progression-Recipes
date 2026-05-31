package dev.sixik.gpr.impl.mixin.recipes.mods.electrodynamics;

import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import electrodynamics.common.recipe.categories.fluid2fluid.specificmachines.ElectrolosisChamberRecipe;
import electrodynamics.common.tile.machines.TileElectrolosisChamber;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import voltaic.api.multiblock.assemblybased.TileMultiblockController;

import java.util.List;

@Mixin(TileElectrolosisChamber.class)
public abstract class MixinTileElectrolosisChamber extends TileMultiblockController {

    @Shadow
    private static boolean testRecipe(ElectrolosisChamberRecipe recipe, FluidTank[] inputTanks) {
        throw new UnsupportedOperationException();
    }

    private MixinTileElectrolosisChamber(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
        super(tileEntityTypeIn, worldPos, blockState);
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"))
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> gpr$checkConditions(
            RecipeManager instance, RecipeType<T> recipeType
    ) {
        return RecipeStageUtils.filterRecipes(instance.getAllRecipesFor(recipeType), this);
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lelectrodynamics/common/tile/machines/TileElectrolosisChamber;testRecipe(Lelectrodynamics/common/recipe/categories/fluid2fluid/specificmachines/ElectrolosisChamberRecipe;[Lnet/neoforged/neoforge/fluids/capability/templates/FluidTank;)Z", ordinal = 1))
    public boolean gpr$checkConditions$check(
            ElectrolosisChamberRecipe recipe, FluidTank[] inputTanks
    ) {
        if (RecipeStageUtils.isUnlocked(this, recipe)) {
            return testRecipe(recipe, inputTanks);
        }
        return false;
    }
}
