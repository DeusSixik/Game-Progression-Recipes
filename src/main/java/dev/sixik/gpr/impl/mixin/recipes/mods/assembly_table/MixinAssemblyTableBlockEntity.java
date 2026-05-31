package dev.sixik.gpr.impl.mixin.recipes.mods.assembly_table;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sixik.assemblytable.blockentity.AssemblyTableBlockEntity;
import dev.sixik.assemblytable.recipes.AssemblyTableRecipe;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AssemblyTableBlockEntity.class)
public abstract class MixinAssemblyTableBlockEntity extends BlockEntity {

    private MixinAssemblyTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "updateRecipeStates(Z)V", at = @At(value = "NEW", target = "()Ljava/util/HashSet;", ordinal = 0))
    public void gpr$updateRecipeStates(boolean allowAutoActivation, CallbackInfo ci,
                                       @Local(type = ObjectArrayList.class, ordinal = 0)
                                       ObjectArrayList<RecipeHolder<AssemblyTableRecipe>> possibleHolders
    ) {
        List<RecipeHolder<AssemblyTableRecipe>> newList = RecipeStageUtils.filterRecipes(possibleHolders, this);
        possibleHolders.clear();
        possibleHolders.addAll(newList);
    }
}
