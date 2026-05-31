package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.MetalPressLogic;
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

@Mixin(MetalPressLogic.class)
public class MixinMetalPressLogic implements IMultiblockLogicPath {

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

    @Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/crafting/MetalPressRecipe;findRecipe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public RecipeHolder<MetalPressRecipe> gpr$onEntityCollision(ItemStack mold, ItemStack input, Level world) {
        RecipeHolder<MetalPressRecipe> recipe = MetalPressRecipe.findRecipe(mold, input, world);
        if(RecipeStageUtils.isUnlocked(gpr$owner, recipe))
            return recipe;

        return null;
    }
}
