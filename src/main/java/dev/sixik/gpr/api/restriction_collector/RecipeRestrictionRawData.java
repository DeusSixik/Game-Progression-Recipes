package dev.sixik.gpr.api.restriction_collector;

import dev.sixik.gpr.api.RecipeRegisterType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public record RecipeRestrictionRawData(
        RecipeRegisterType registerType,
        Object stage,
        @Nullable BlockEntityType<?> block,
        @Nullable RecipeType<?> recipeType,
        ResourceLocation[] recipes
) {

}
