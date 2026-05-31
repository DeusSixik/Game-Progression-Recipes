package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AutoWorkbenchLogic;
import blusunrize.immersiveengineering.common.items.EngineersBlueprintItem;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.UUID;

import static blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AutoWorkbenchLogic.BLUEPRINT_SLOT;

@Mixin(AutoWorkbenchLogic.class)
public class MixinAutoWorkbenchLogic implements IMultiblockLogicPath {

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/AutoWorkbenchLogic;getAvailableRecipes(Lnet/minecraft/world/level/Level;Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/AutoWorkbenchLogic$State;)Ljava/util/List;"))
    public List<RecipeHolder<BlueprintCraftingRecipe>> gpr$tickServer(Level level, AutoWorkbenchLogic.State state) {
        return RecipeStageUtils.filterRecipes(EngineersBlueprintItem.getRecipes(level, state.inventory.getStackInSlot(BLUEPRINT_SLOT)), gpr$owner);
    }

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
}
