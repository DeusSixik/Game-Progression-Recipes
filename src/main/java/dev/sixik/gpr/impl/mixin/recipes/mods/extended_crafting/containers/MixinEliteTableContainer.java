package dev.sixik.gpr.impl.mixin.recipes.mods.extended_crafting.containers;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.container.EliteTableContainer;
import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EliteTableContainer.class)
public abstract class MixinEliteTableContainer extends BaseContainerMenu {

    @Unique
    private Player gpr$player;

    private MixinEliteTableContainer(MenuType<?> menu, int id, BlockPos pos) {
        super(menu, id, pos);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lcom/blakebr0/cucumber/inventory/BaseItemStackHandler;Lnet/minecraft/core/BlockPos;)V",
    at = @At("RETURN"))
    public void gpr$init(MenuType type, int id, Inventory playerInventory, BaseItemStackHandler inventory, BlockPos pos, CallbackInfo ci) {
        gpr$player = playerInventory.player;
    }

    @Redirect(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> gpr$slotsChanged$redirect(
            RecipeManager instance, RecipeType<T> recipeType, I input, Level level
    ) {
        Optional<RecipeHolder<T>> recipe = instance.getRecipeFor(recipeType, input, level);
        if(recipe.isPresent()) {
            RecipeHolder<T> valueRecipe = recipe.get();
            if(RecipeStageUtils.isUnlocked(gpr$player, valueRecipe)) {
                return recipe;
            }

            return Optional.empty();
        }

        return recipe;
    }
}
