package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AlloySmelterLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AlloySmelterLogic.State.class)
public abstract class MixinAlloySmelterLogic$State
        implements IMultiblockState, FurnaceHandler.IFurnaceEnvironment<AlloyRecipe>, IMultiblockStatePath {

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

    @Inject(method = "getRecipeForInput()Lblusunrize/immersiveengineering/api/crafting/AlloyRecipe;", at = @At("RETURN"), cancellable = true)
    public void gpr$getRecipe(CallbackInfoReturnable<AlloyRecipe> cir) {
        if(!RecipeStageUtils.isUnlocked(gpr$owner, cir.getReturnValue())) {
            cir.setReturnValue(null);
        }
    }
}
