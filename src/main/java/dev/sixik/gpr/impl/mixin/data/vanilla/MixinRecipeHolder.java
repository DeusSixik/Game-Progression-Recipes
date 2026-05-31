package dev.sixik.gpr.impl.mixin.data.vanilla;

import dev.sixik.gpr.api.RecipeIndex;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeHolder.class)
public class MixinRecipeHolder implements RecipeIndex {

    @Unique
    private int gpr$index = -1;

    @Override
    public int gpr$getIndex() {
        return gpr$index;
    }

    @Override
    public void gpr$setIndex(int index) {
        this.gpr$index = index;
    }
}
