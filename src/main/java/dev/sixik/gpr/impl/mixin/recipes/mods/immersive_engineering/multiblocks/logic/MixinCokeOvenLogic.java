package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(CokeOvenLogic.class)
public class MixinCokeOvenLogic implements IMultiblockLogicPath {

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

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void gpr$getRecipe(IMultiblockContext<CokeOvenLogic.State> context, CallbackInfoReturnable<CokeOvenRecipe> cir) {
        CokeOvenRecipe recipe = cir.getReturnValue();
        if(recipe == null) return;

        if(RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
            return;
        }
        cir.setReturnValue(null);
    }
}
