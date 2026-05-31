package dev.sixik.gpr.impl.mixin.recipes.mods.alchemistry;

import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedList;

@Mixin(FissionControllerBlockEntity.class)
public abstract class MixinFissionControllerBlockEntity extends AbstractReactorBlockEntity {

    @Shadow
    private FissionRecipe currentRecipe;

    private MixinFissionControllerBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    public void updateRecipe() {
        if (this.level != null && !this.level.isClientSide() && !this.isRecipeLocked()) {
            FissionRecipe nextRecipe = null;
            if (!this.getInputHandler().isEmpty()) {
                FissionRecipe newRecipe = RecipeRegistry.getFissionRecipe((recipe) ->
                        ItemStack.isSameItemSameComponents(recipe.getInput(), this.getInputHandler().getStackInSlot(0)), this.level)
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
    public @NotNull LinkedList<FissionRecipe> getAllRecipes() {
        return this.level != null ? new LinkedList(
                RecipeStageUtils.filterRecipesValue(
                        RecipeRegistry.getFissionRecipes(this.level), BlockEntityOwner.get(this).gpr$getOwner(), 0
                )
        ) : new LinkedList();
    }
}
