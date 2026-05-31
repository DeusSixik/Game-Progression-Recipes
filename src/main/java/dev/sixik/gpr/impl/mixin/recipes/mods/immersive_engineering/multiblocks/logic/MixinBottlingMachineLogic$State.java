package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.BottlingMachineRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.bottling_machine.BottlingMachineLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.IntToDoubleFunction;

@Mixin(BottlingMachineLogic.State.class)
public abstract class MixinBottlingMachineLogic$State
        implements IMultiblockState, ProcessContext.ProcessContextInWorld<BottlingMachineRecipe>, IMultiblockStatePath {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(ILjava/util/function/IntToDoubleFunction;ILjava/lang/Runnable;Ljava/lang/Runnable;Ljava/util/function/BiFunction;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcessor$InWorldProcessor;"))
    public MultiblockProcessor.InWorldProcessor<BottlingMachineRecipe> gpr$init(int maxQueueLength, IntToDoubleFunction minDelayAfter, int maxProcessPerTick, Runnable markDirty, Runnable onQueueChange, BiFunction getRecipeFromID) {

        return new MultiblockProcessor.InWorldProcessor<>(
                maxQueueLength, minDelayAfter, maxProcessPerTick, markDirty, onQueueChange,
                (level, name) -> {
                    var recipe = BottlingMachineRecipe.RECIPES.getById(level, name);
                    if (RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
                        return recipe;
                    }
                    return null;
                }
        );
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
