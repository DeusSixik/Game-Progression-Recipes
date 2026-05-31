package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(ArcFurnaceLogic.State.class)
public abstract class MixinArcFurnaceLogic$State
        implements IMultiblockState, ProcessContext.ProcessContextInMachine<ArcFurnaceRecipe>, IMultiblockStatePath {

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

    @Redirect(method = {"additionalCanProcessCheck", "onProcessFinish"}, at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/blocks/multiblocks/process/MultiblockProcess;getRecipe(Lnet/minecraft/world/level/Level;)Lblusunrize/immersiveengineering/api/crafting/MultiblockRecipe;"))
    public <R extends MultiblockRecipe, CTX extends ProcessContext<R>> R gpr$getRecipe(
            MultiblockProcess<R, CTX> instance, Level level
    ) {
        R recipe = instance.getRecipe(level);
        if(!RecipeStageUtils.isUnlocked(gpr$owner, recipe)) {
            return null;
        }
        return recipe;
    }
}
