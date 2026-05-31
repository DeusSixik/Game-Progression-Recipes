package dev.sixik.gpr.impl.mixin.recipes.mods.extended_crafting;

import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import com.blakebr0.extendedcrafting.tileentity.EnderCrafterTileEntity;
import dev.sixik.gpr.impl.compat.extendedcrafting.ExtendedCraftingCachedRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderCrafterTileEntity.class)
public abstract class MixinEnderCrafterTileEntity extends BaseInventoryTileEntity implements MenuProvider {

    private MixinEnderCrafterTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
    at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/crafting/RecipeType;)Lcom/blakebr0/cucumber/inventory/CachedRecipe;"))
    public CachedRecipe gpr$init(RecipeType type) {
        return new ExtendedCraftingCachedRecipe(this, type);
    }
}
