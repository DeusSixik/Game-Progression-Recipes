package dev.sixik.gpr.impl.mixin.recipes.mods.alchemistry;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedList;

@Mixin(AtomizerBlockEntity.class)
public abstract class MixinAtomizerBlockEntity extends AbstractFluidBlockEntity {

    @Shadow
    private AtomizerRecipe currentRecipe;

    private MixinAtomizerBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    public void updateRecipe() {
        if (this.level != null && !this.level.isClientSide()) {
            AtomizerRecipe nextRecipe = null;
            if (!this.getFluidStorage().isEmpty()) {
                AtomizerRecipe newRecipe = RecipeRegistry.getAtomizerRecipe((recipe) ->
                                recipe.getInput().getFluid().equals(this.getFluidStorage().getFluidStack().getFluid()), this.level)
                        .orElse(null);
                if(RecipeStageUtils.isUnlocked(this, newRecipe))
                    nextRecipe = newRecipe;
            }

            if (nextRecipe != null) {
                if (this.currentRecipe == null || !this.currentRecipe.equals(nextRecipe)) {
                    this.setProgress(0);
                    this.setRecipe(nextRecipe.copy());
                }
            } else if (this.currentRecipe != null) {
                this.setProgress(0);
                this.setRecipe(null);
            }
        }

    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    @SuppressWarnings("all")
    public @NotNull LinkedList<AtomizerRecipe> getAllRecipes() {
        return this.level != null ? new LinkedList(
                RecipeStageUtils.filterRecipesValue(
                        RecipeRegistry.getAtomizerRecipes(this.level), BlockEntityOwner.get(this).gpr$getOwner(), 0
                )
        ) : new LinkedList();
    }
}
