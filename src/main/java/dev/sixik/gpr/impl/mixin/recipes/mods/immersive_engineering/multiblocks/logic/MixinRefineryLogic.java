package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.RefineryLogic;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(RefineryLogic.class)
public class MixinRefineryLogic implements IMultiblockLogicPath {

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

    @Redirect(method = "tryEnqueueProcess", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/crafting/RefineryRecipe;findRecipe(Lnet/minecraft/world/level/Level;Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public RecipeHolder<RefineryRecipe> gpr$tryEnqueueProcess(Level f1, FluidStack f2, FluidStack f3, ItemStack f4) {
        var recipe = RefineryRecipe.findRecipe(f1, f2, f3, f4);
        if(RecipeStageUtils.isUnlocked(gpr$owner, recipe))
            return recipe;

        return null;
    }
}
