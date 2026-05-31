package dev.sixik.gpr.impl.mixin.recipes.mods.mekanism;

import dev.sixik.gpr.impl.utils.RecipeStageUtils;
import mekanism.api.IContentsListener;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ICachedRecipeHolder;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeCacheLookupMonitor.class)
public abstract class MixinRecipeCacheLookupMonitor<RECIPE extends MekanismRecipe<?>> implements ICachedRecipeHolder<RECIPE>, IContentsListener {


    @Shadow
    protected CachedRecipe<RECIPE> cachedRecipe;

    @Shadow
    @Final
    protected int cacheIndex;

    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @Shadow
    protected boolean shouldUnpause;

    /**
     * @author Sixik
     * @reason
     */
    @Overwrite
    public boolean updateAndProcess() {
        CachedRecipe<RECIPE> oldCache = this.cachedRecipe;
        this.cachedRecipe = this.getUpdatedCache(this.cacheIndex);

        if(cachedRecipe != null && handler instanceof BlockEntity entity) {
            final RECIPE cacheRecipe = cachedRecipe.getRecipe();

            if(!RecipeStageUtils.isUnlocked(entity, cacheRecipe))
                return false;
        }

        if (this.cachedRecipe != oldCache) {
            this.handler.onCachedRecipeChanged(this.cachedRecipe, this.cacheIndex);
        }

        if (this.cachedRecipe != null) {
            if (this.shouldUnpause) {
                this.shouldUnpause = false;
                this.cachedRecipe.unpauseErrors();
            }

            this.cachedRecipe.process();
            return true;
        } else {
            return false;
        }
    }
}
