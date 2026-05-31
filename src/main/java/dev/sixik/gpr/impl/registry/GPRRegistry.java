package dev.sixik.gpr.impl.registry;

import dev.sixik.gpr.api.RecipeTypeSupport;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class GPRRegistry {

    public static GPRRegistry INSTANCE = new GPRRegistry();

    private GPRRegistry() {}

    public void clearData() {

    }

    public void registerData() {

    }

    private void applyRecipeTypesSupports(List<RecipeType<?>> registeredTypes) {
        for (RecipeType<?> recipeType : BuiltInRegistries.RECIPE_TYPE) {
            if(recipeType instanceof RecipeTypeSupport support) {
                for (RecipeType<?> registeredType : registeredTypes) {
                    if(registeredType.equals(recipeType)) {
                        support.gpr$setHasRestrictions(true);
                        break;
                    }
                }
            }
        }
    }
}
