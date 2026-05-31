package dev.sixik.gpr.impl.mixin.recipes.mods.draconic_evolution;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.IChangeListener;
import com.brandon3055.brandonscore.lib.IInteractTile;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(TileFusionCraftingCore.class)
public abstract class MixinTileFusionCraftingCore extends TileBCore implements IFusionInventory, IFusionStateMachine, MenuProvider, IInteractTile, IChangeListener {

    private MixinTileFusionCraftingCore(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "startCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> gpr$startCraft
            (RecipeManager instance, RecipeType<T> recipeType, I input, Level level) {
        Optional<RecipeHolder<T>> recipeHolder = instance.getRecipeFor(recipeType, input, level);;
        return RecipeStageUtils.isUnlocked(this, recipeHolder.orElse(null)) ? recipeHolder : null;
    }
}
