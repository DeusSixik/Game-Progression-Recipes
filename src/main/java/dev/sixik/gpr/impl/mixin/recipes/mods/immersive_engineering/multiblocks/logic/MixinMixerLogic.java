package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(MixerLogic.class)
public abstract class MixinMixerLogic implements IMultiblockLogicPath {

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

    @Redirect(method = "enqueueNewRecipes", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/crafting/MixerRecipe;findRecipe(Lnet/minecraft/world/level/Level;Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/minecraft/core/NonNullList;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public RecipeHolder<MixerRecipe> gpr$enqueueNewRecipes(Level level, FluidStack fluidStack, NonNullList<ItemStack> fluid) {
        var recipe = MixerRecipe.findRecipe(level, fluidStack, fluid);
        if(RecipeStageUtils.isUnlocked(gpr$owner, recipe))
            return recipe;

        return null;
    }
}
