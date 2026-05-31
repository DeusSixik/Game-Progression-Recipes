package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.SqueezerLogic;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(SqueezerLogic.class)
public class MixinSqueezerLogic implements IMultiblockLogicPath {

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

    @Redirect(method = "enqueueProcesses", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/crafting/SqueezerRecipe;findRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public RecipeHolder<SqueezerRecipe> gpr$enqueueProcesses(Level level, ItemStack stack) {
        var recipe = SqueezerRecipe.findRecipe(level, stack);
        if(RecipeStageUtils.isUnlocked(gpr$owner, recipe))
            return recipe;

        return null;
    }
}
