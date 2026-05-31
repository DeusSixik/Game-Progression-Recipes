package dev.sixik.gpr.impl.mixin.recipes.mods.alchemistry;

import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
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

@Mixin(LiquifierBlockEntity.class)
public abstract class MixinLiquifierBlockEntity extends AbstractFluidBlockEntity {
    @Shadow
    private LiquifierRecipe currentRecipe;

    private MixinLiquifierBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    public void updateRecipe() {
        if (this.level != null && !this.level.isClientSide()) {
            LiquifierRecipe nextRecipe = null;
            if (!this.getInputHandler().isEmpty()) {
                var newRecipe = RecipeRegistry.getLiquifierRecipe((recipe) -> recipe.getInput().matches(this.getInputHandler().getStackInSlot(0)), this.level)
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
                this.setRecipe(null);
            }
        }
    }

    @Overwrite
    @SuppressWarnings("all")
    public @NotNull LinkedList<LiquifierRecipe> getAllRecipes() {
        return this.level != null ? new LinkedList(
                RecipeStageUtils.filterRecipesValue(
                        RecipeRegistry.getLiquifierRecipes(this.level), BlockEntityOwner.get(this).gpr$getOwner(), 0
                )
        ) : new LinkedList();
    }
}
