package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CrusherLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.DirectProcessingItemHandler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Mixin(CrusherLogic.State.class)
public class MixinCrusherLogicState implements IMultiblockStatePath  {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(IFILjava/lang/Runnable;Ljava/util/function/BiFunction;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcessor;"))
    public MultiblockProcessor<CrusherRecipe, ProcessContext.ProcessContextInWorld<CrusherRecipe>> gpr$init$processor(int maxQueueLength, float minDelayAfter, int maxProcessPerTick, Runnable markDirty, BiFunction getRecipeFromID) {

        return new MultiblockProcessor<>(
                maxQueueLength, minDelayAfter, maxProcessPerTick, markDirty,
                (level, name) -> {
                    var recipe = CrusherRecipe.RECIPES.getById(level, name);
                    if (RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
                        return recipe;
                    }
                    return null;
                }
        );
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Ljava/util/function/Supplier;Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcessor;Ljava/util/function/BiFunction;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/DirectProcessingItemHandler;"))
    public DirectProcessingItemHandler gpr$init$insertionHandler(Supplier level, MultiblockProcessor processor, BiFunction getRecipeOnInsert) {
        return new DirectProcessingItemHandler<>(
                level, processor,
                (levelF, name) -> {
                    RecipeHolder<CrusherRecipe> recipe = CrusherRecipe.findRecipe(levelF, name);
                    if (RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
                        return recipe;
                    }
                    return null;
                }
        ).setProcessStacking(true);
    }

    @Unique
    private UUID gpr$owner;

    @Override
    public UUID gpr$getOwner() {
        return gpr$owner;
    }

    @Override
    public void gpr$setOwner(UUID owner) {
        this.gpr$owner = owner;
    }
}
