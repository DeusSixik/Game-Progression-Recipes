package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.BiFunction;

@Mixin(MixerLogic.State.class)
public abstract class MixinMixerLogic$State implements IMultiblockStatePath {

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

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(IFILjava/lang/Runnable;Ljava/util/function/BiFunction;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcessor$InMachineProcessor;"))
    public MultiblockProcessor.InMachineProcessor gpr$init(int maxQueueLength, float minDelayAfter, int maxProcessPerTick, Runnable markDirty, BiFunction getRecipeFromID) {
        return new MultiblockProcessor.InMachineProcessor<>(
                maxQueueLength, minDelayAfter, maxProcessPerTick, markDirty,
                (levelF, name) -> {
                    MixerRecipe recipe = MixerRecipe.RECIPES.getById(levelF, name);
                    if (RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
                        return recipe;
                    }
                    return null;
                }
        );
    }
}
