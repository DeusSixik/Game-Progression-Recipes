package dev.sixik.gpr.impl.mixin.recipes.mods.electrodynamics;

import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import electrodynamics.common.tile.machines.arcfurnace.TileElectricArcFurnace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import voltaic.prefab.tile.GenericTile;

import java.util.List;

@Mixin(TileElectricArcFurnace.class)
public abstract class MixinTileElectricArcFurnace extends GenericTile {

    private MixinTileElectricArcFurnace(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
        super(tileEntityTypeIn, worldPos, blockState);
    }

    @Redirect(method = "checkConditions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"))
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> gpr$checkConditions(
            RecipeManager instance, RecipeType<T> recipeType
    ) {
        return RecipeStageUtils.filterRecipes(instance.getAllRecipesFor(recipeType), this);
    }
}
