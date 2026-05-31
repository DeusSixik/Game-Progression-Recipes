package dev.sixik.gpr.impl.compat.jei.hider_pipeline;

import dev.sixik.gpf.impl.client.ClientStageData;
import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.api.RecipeManagerExtern;
import dev.sixik.gpr.impl.events.GPRClientEvents;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import dev.sixik.gpr.impl.registry.RecipeRestrictionData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JeiRecipeHideEntrypoint {

    private static final ObjectOpenHashSet<ResourceLocation> PREVIOUSLY_MANAGED_RECIPES = new ObjectOpenHashSet<>();

    private JeiRecipeHideEntrypoint() {
    }

    public static void invoke(IJeiRuntime runtime) {
        if (runtime == null) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        final Level level = minecraft.level;
        if (level == null) {
            return;
        }

        final RecipeManagerExtern vanillaRecipeManager = RecipeManagerExtern.get(level.getRecipeManager());
        final GPRRegistry data = GPRClientEvents.CLIENT_DATA;
        final ObjectCollection<RecipeRestrictionData> restrictionsData = data.getRestrictions().values();

        final ObjectOpenHashSet<ResourceLocation> managedRecipes = new ObjectOpenHashSet<>();
        final ObjectOpenHashSet<ResourceLocation> unlockedRecipes = new ObjectOpenHashSet<>();

        for (RecipeRestrictionData restriction : restrictionsData) {
            boolean hasStage = ClientStageData.INSTANCE.hasStages(restriction.stage());
            collectRecipeIds(managedRecipes, hasStage ? unlockedRecipes : null, vanillaRecipeManager, restriction);
        }

        final ObjectOpenHashSet<ResourceLocation> hiddenRecipes = new ObjectOpenHashSet<>(managedRecipes);
        hiddenRecipes.removeAll(unlockedRecipes);

        final ObjectOpenHashSet<ResourceLocation> visibleRecipes = new ObjectOpenHashSet<>(PREVIOUSLY_MANAGED_RECIPES);
        visibleRecipes.addAll(unlockedRecipes);
        visibleRecipes.removeAll(hiddenRecipes);

        final IRecipeManager jeiRecipeManager = runtime.getRecipeManager();
        applyVisibility(jeiRecipeManager, vanillaRecipeManager, visibleRecipes, false);
        applyVisibility(jeiRecipeManager, vanillaRecipeManager, hiddenRecipes, true);

        PREVIOUSLY_MANAGED_RECIPES.clear();
        PREVIOUSLY_MANAGED_RECIPES.addAll(managedRecipes);
    }

    private static void collectRecipeIds(
            ObjectOpenHashSet<ResourceLocation> managedRecipes,
            ObjectOpenHashSet<ResourceLocation> unlockedRecipes,
            RecipeManagerExtern manager,
            RecipeRestrictionData restriction
    ) {
        for (int[] recipeIds : restriction.unpackData().values()) {
            for (int recipeId : recipeIds) {
                ResourceLocation recipeKey = manager.getRecipeIdByIndex(recipeId);
                if (recipeKey == null) {
                    GameProgressionRecipes.LOGGER.warn("GPR JEI: Missing recipe id for synced recipe index {}", recipeId);
                    continue;
                }

                managedRecipes.add(recipeKey);
                if (unlockedRecipes != null) {
                    unlockedRecipes.add(recipeKey);
                }
            }
        }
    }

    private static void applyVisibility(
            IRecipeManager jeiRecipeManager,
            RecipeManagerExtern vanillaRecipeManager,
            Collection<ResourceLocation> recipeIds,
            boolean hidden
    ) {
        Map<mezz.jei.api.recipe.RecipeType<?>, ObjectArrayList<RecipeHolder<?>>> groupedRecipes =
                groupRecipesByType(vanillaRecipeManager, recipeIds);

        for (Map.Entry<mezz.jei.api.recipe.RecipeType<?>, ObjectArrayList<RecipeHolder<?>>> entry : groupedRecipes.entrySet()) {
            if (hidden) {
                hideRecipes(jeiRecipeManager, entry.getKey(), entry.getValue());
            } else {
                unhideRecipes(jeiRecipeManager, entry.getKey(), entry.getValue());
            }
        }
    }

    private static Map<mezz.jei.api.recipe.RecipeType<?>, ObjectArrayList<RecipeHolder<?>>> groupRecipesByType(
            RecipeManagerExtern manager,
            Collection<ResourceLocation> recipeIds
    ) {
        Map<mezz.jei.api.recipe.RecipeType<?>, ObjectArrayList<RecipeHolder<?>>> groupedRecipes = new LinkedHashMap<>();

        for (ResourceLocation recipeId : recipeIds) {
            int recipeIndex = manager.getRecipeIndex(recipeId);
            if (recipeIndex < 0) {
                continue;
            }

            RecipeHolder<?> recipeHolder = manager.getRecipeHolderByIndex(recipeIndex);
            if (recipeHolder == null) {
                continue;
            }

            mezz.jei.api.recipe.RecipeType<?> jeiRecipeType =
                    mezz.jei.api.recipe.RecipeType.createFromVanilla(recipeHolder.value().getType());
            groupedRecipes.computeIfAbsent(jeiRecipeType, ignored -> new ObjectArrayList<>()).add(recipeHolder);
        }

        return groupedRecipes;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void hideRecipes(
            IRecipeManager jeiRecipeManager,
            mezz.jei.api.recipe.RecipeType<?> recipeType,
            Collection<RecipeHolder<?>> recipes
    ) {
        jeiRecipeManager.hideRecipes((mezz.jei.api.recipe.RecipeType) recipeType, (Collection) recipes);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void unhideRecipes(
            IRecipeManager jeiRecipeManager,
            mezz.jei.api.recipe.RecipeType<?> recipeType,
            Collection<RecipeHolder<?>> recipes
    ) {
        jeiRecipeManager.unhideRecipes((mezz.jei.api.recipe.RecipeType) recipeType, (Collection) recipes);
    }
}
