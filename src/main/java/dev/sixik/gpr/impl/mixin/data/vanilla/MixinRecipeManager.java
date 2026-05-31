package dev.sixik.gpr.impl.mixin.data.vanilla;

import dev.sixik.gpr.api.RecipeIndex;
import dev.sixik.gpr.api.RecipeManagerExtern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements RecipeManagerExtern {

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Unique
    private static final Comparator<Map.Entry<ResourceLocation, RecipeHolder<?>>> GPR$RECIPE_ORDER =
            Comparator.comparing(Map.Entry::getKey);

    @Unique
    private volatile RecipeHolder[] holdersCache;

    @Unique
    private volatile Recipe[] recipesCache;

    @Unique
    private volatile Map<ResourceLocation, Integer> recipeIndexCache;

    @Unique
    private volatile ResourceLocation[] recipeIdsCache;

    @Override
    public RecipeHolder[] getRecipeHoldersRaw() {
        if (holdersCache == null) {
            gpr$initCache();
        }

        return holdersCache;
    }

    @Override
    public Recipe[] getRecipesRaw() {
        if (recipesCache == null) {
            gpr$initCache();
        }

        return recipesCache;
    }

    @Override
    public int getRecipeIndex(ResourceLocation id) {
        if (recipeIndexCache == null) {
            gpr$initCache();
        }

        Integer index = recipeIndexCache.get(id);
        return index == null ? -1 : index;
    }

    @Override
    public RecipeHolder<?> getRecipeHolderByIndex(int index) {
        RecipeHolder[] holders = getRecipeHoldersRaw();
        return index >= 0 && index < holders.length ? holders[index] : null;
    }

    @Override
    public ResourceLocation getRecipeIdByIndex(int index) {
        if (recipeIdsCache == null) {
            gpr$initCache();
        }

        ResourceLocation[] ids = recipeIdsCache;
        return index >= 0 && index < ids.length ? ids[index] : null;
    }

    @Inject(method = {"apply", "replaceRecipes"}, at = @At("HEAD"))
    private void gpr$invalidateCache(CallbackInfo ci) {
        holdersCache = null;
        recipesCache = null;
        recipeIndexCache = null;
        recipeIdsCache = null;
    }

    @Unique
    private void gpr$initCache() {
        if (holdersCache != null && recipesCache != null && recipeIndexCache != null && recipeIdsCache != null) {
            return;
        }

        synchronized (this) {
            if (holdersCache != null && recipesCache != null && recipeIndexCache != null && recipeIdsCache != null) {
                return;
            }

            List<Map.Entry<ResourceLocation, RecipeHolder<?>>> sortedEntries = new ArrayList<>(byName.entrySet());
            sortedEntries.sort(GPR$RECIPE_ORDER);

            RecipeHolder[] holdersCache = new RecipeHolder[sortedEntries.size()];
            Recipe[] recipesCache = new Recipe[sortedEntries.size()];
            Map<ResourceLocation, Integer> recipeIndexCache = new HashMap<>(sortedEntries.size());
            ResourceLocation[] recipeIdsCache = new ResourceLocation[sortedEntries.size()];

            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<ResourceLocation, RecipeHolder<?>> entry = sortedEntries.get(i);
                RecipeHolder<?> holder = entry.getValue();
                Recipe<?> recipe = holder.value();

                holdersCache[i] = holder;
                recipesCache[i] = recipe;
                recipeIndexCache.put(entry.getKey(), i);
                recipeIdsCache[i] = entry.getKey();

                RecipeIndex.get(holder).gpr$setIndex(i);
                RecipeIndex.get(recipe).gpr$setIndex(i);
            }

            this.holdersCache = holdersCache;
            this.recipesCache = recipesCache;
            this.recipeIndexCache = recipeIndexCache;
            this.recipeIdsCache = recipeIdsCache;
        }
    }
}
