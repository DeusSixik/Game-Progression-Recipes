package dev.sixik.gpr.impl.mixin.recipes.mods.applied_energistics.extended_ae;

import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.glodium.recipe.CommonRecipeContext;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(targets = "com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler$CrystalRecipeContext")
public abstract class MixinTileCrystalAssembler extends CommonRecipeContext<CrystalAssemblerRecipe> {

    @Shadow
    @Final
    private TileCrystalAssembler host;

    private MixinTileCrystalAssembler(Supplier<Level> levelGetter, RecipeType<CrystalAssemblerRecipe> type) {
        super(levelGetter, type);
    }

    @Inject(method = "testRecipe", at = @At("HEAD"), cancellable = true)
    public void gpr$testRecipe(RecipeHolder<CrystalAssemblerRecipe> recipe, CallbackInfoReturnable<Boolean> cir) {
        if(!RecipeStageUtils.isUnlocked(host, recipe))
            cir.setReturnValue(false);
    }
}
