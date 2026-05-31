package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic;
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
import java.util.function.IntToDoubleFunction;

@Mixin(MetalPressLogic.State.class)
public class MixinMetalPressLogic$State implements IMultiblockStatePath {

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

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(ILjava/util/function/IntToDoubleFunction;ILjava/lang/Runnable;Ljava/lang/Runnable;Ljava/util/function/BiFunction;)Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcessor;"))
    public MultiblockProcessor gpr$init(int maxQueueLength, IntToDoubleFunction minDelayAfter, int maxProcessPerTick, Runnable markDirty, Runnable onQueueChange, BiFunction getRecipeFromID) {
        return new MultiblockProcessor<>(
                maxQueueLength, minDelayAfter, maxProcessPerTick,
                markDirty, onQueueChange,
                (levelF, name) -> {
                    MetalPressRecipe recipe = MetalPressRecipe.STANDARD_RECIPES.getById(levelF, name);
                    if (RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
                        return recipe;
                    }
                    return null;
                }
        );
    }
}
