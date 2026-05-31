package dev.sixik.gpr.impl.mixin.recipes.mods.alchemistry;

import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
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
import java.util.function.Predicate;

@Mixin(FusionControllerBlockEntity.class)
public abstract class MixinFusionControllerBlockEntity extends AbstractReactorBlockEntity {

    @Shadow
    private FusionRecipe currentRecipe;

    private MixinFusionControllerBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
    }

    /**
     * @author Sixik
     * @reason Add Stage support
     */
    @Overwrite
    public void updateRecipe() {
        if (this.level != null && !this.level.isClientSide() && !this.isRecipeLocked()) {
            FusionRecipe nextRecipe = null;
            if (!this.getInputHandler().isEmpty()) {
                Predicate<FusionRecipe> recipePredicate = (recipe) -> {
                    ItemStack input1 = this.getInputHandler().getStackInSlot(0);
                    ItemStack input2 = this.getInputHandler().getStackInSlot(1);
                    return ItemStack.isSameItemSameComponents(recipe.getInput1(), input1) && ItemStack.isSameItemSameComponents(recipe.getInput2(), input2) || ItemStack.isSameItemSameComponents(recipe.getInput2(), input1) && ItemStack.isSameItemSameComponents(recipe.getInput1(), input2);
                };
                var newRecipe = RecipeRegistry.getFusionRecipe(recipePredicate, this.level).orElse(null);
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
    public @NotNull LinkedList<FusionRecipe> getAllRecipes() {
        return this.level != null ? new LinkedList(
                RecipeStageUtils.filterRecipesValue(
                        RecipeRegistry.getFusionRecipes(this.level), BlockEntityOwner.get(this).gpr$getOwner(), 0
                )
        ) : new LinkedList();
    }
}
