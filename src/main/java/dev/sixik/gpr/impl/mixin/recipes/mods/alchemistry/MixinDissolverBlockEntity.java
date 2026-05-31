package dev.sixik.gpr.impl.mixin.recipes.mods.alchemistry;

import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
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

@Mixin(DissolverBlockEntity.class)
public abstract class MixinDissolverBlockEntity extends AbstractInventoryBlockEntity {

    @Shadow
    private DissolverRecipe currentRecipe;

    private MixinDissolverBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    public void updateRecipe() {
        if (this.level != null && !this.level.isClientSide() && !this.isRecipeLocked()) {
            DissolverRecipe nextRecipe = null;
            if (!this.getInputHandler().getStackInSlot(0).isEmpty()) {
                DissolverRecipe newRecipe = RecipeRegistry.getDissolverRecipe((recipe) ->
                        recipe.matches(this.getInputHandler().getStackInSlot(0)), this.level)
                        .orElse(null);
                if(RecipeStageUtils.isUnlocked(this, newRecipe)) {
                    nextRecipe = newRecipe;
                }
            }

            if (nextRecipe != null) {
                if (this.currentRecipe == null || !this.currentRecipe.equals(nextRecipe)) {
                    this.setProgress(0);
                    this.setRecipe(nextRecipe.copy());
                }
            } else if (this.currentRecipe != null) {
                this.setProgress(0);
                this.setRecipe((AbstractProcessingRecipe)null);
            }
        }

    }

    @Overwrite
    @SuppressWarnings("all")
    public @NotNull LinkedList<DissolverRecipe> getAllRecipes() {
        return this.level != null ? new LinkedList(
                RecipeStageUtils.filterRecipesValue(
                        RecipeRegistry.getDissolverRecipes(this.level), BlockEntityOwner.get(this).gpr$getOwner(), 0
                )
        ) : new LinkedList();
    }
}
