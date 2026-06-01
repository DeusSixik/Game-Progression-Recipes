package dev.sixik.gpr.impl.mixin.recipes.mods.nuclear_craft;

import com.nred.nuclearcraft.block_entity.ITickable;
import com.nred.nuclearcraft.block_entity.ITileGui;
import com.nred.nuclearcraft.block_entity.fluid.ITileFluid;
import com.nred.nuclearcraft.block_entity.inventory.ITileInventory;
import com.nred.nuclearcraft.block_entity.processor.IProcessor;
import com.nred.nuclearcraft.block_entity.processor.info.ProcessorMenuInfo;
import com.nred.nuclearcraft.payload.processor.ProcessorUpdatePacket;
import com.nred.nuclearcraft.recipe.BasicRecipe;
import com.nred.nuclearcraft.recipe.RecipeInfo;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(IProcessor.class)
public interface MixinIProcessor <TILE extends BlockEntity & IProcessor<TILE, PACKET, INFO>, PACKET extends ProcessorUpdatePacket, INFO extends ProcessorMenuInfo<TILE, PACKET, INFO>> extends ITickable, ITileInventory, ITileFluid, ITileGui<TILE, PACKET, INFO> {

    @Shadow
    void setRecipeInfo(RecipeInfo<? extends BasicRecipe> recipeInfo);

    @Redirect(method = "refreshRecipe", at = @At(value = "INVOKE", target = "Lcom/nred/nuclearcraft/block_entity/processor/IProcessor;setRecipeInfo(Lcom/nred/nuclearcraft/recipe/RecipeInfo;)V"))
    default void gpr$refreshRecipe(IProcessor instance, RecipeInfo<? extends BasicRecipe> recipeInfo) {
        if(!(this instanceof BlockEntity entity) || recipeInfo == null) {
            setRecipeInfo(recipeInfo);
            return;
        }

        UUID owner = BlockEntityOwner.get(entity).gpr$getOwner();
        if(RecipeStageUtils.isUnlocked(owner, recipeInfo.recipe)) {
            setRecipeInfo(recipeInfo);
            return;
        }

        setRecipeInfo(null);
    }
}
