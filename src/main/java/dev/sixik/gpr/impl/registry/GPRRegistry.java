package dev.sixik.gpr.impl.registry;

import dev.sixik.gpf.api.Stages;
import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.api.BlockEntityTypeIndex;
import dev.sixik.gpr.api.RecipeManagerExtern;
import dev.sixik.gpr.api.RecipeRegisterType;
import dev.sixik.gpr.api.RecipeTypeIndex;
import dev.sixik.gpr.api.RecipeTypeSupport;
import dev.sixik.gpr.api.restriction_collector.RecipeRestrictionRawData;
import dev.sixik.gpr.impl.events.custom.RegisterRestrictionErrorEvent;
import dev.sixik.gpr.impl.utils.GPRecipeRegisterHelper;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortLinkedOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class GPRRegistry {

    public static GPRRegistry INSTANCE = new GPRRegistry();

    private final Long2ObjectMap<RecipeRestrictionData> restrictions = new Long2ObjectLinkedOpenHashMap<>();
    private final ObjectOpenHashSet<RecipeRestrictionRawData> pendingData = new ObjectOpenHashSet<>();

    private short[] knowStages;
    private Int2ShortOpenHashMap recipeByStage = new Int2ShortOpenHashMap();

    private GPRRegistry() {}

    public void clearData() {
        pendingData.clear();
        restrictions.clear();
        recipeByStage = new Int2ShortOpenHashMap();
        recipeByStage.defaultReturnValue((short) -1);
        knowStages = null;
    }

    public void registerData() {
        restrictions.clear();

        ShortList knowStagesList = new ShortArrayList();
        Int2ShortOpenHashMap recipeByStage = new Int2ShortOpenHashMap();
        recipeByStage.defaultReturnValue((short) -1);
        try {
            RecipeManagerExtern managerExtern = RecipeManagerExtern.get(GPRecipeRegisterHelper.getRecipeManager());
            Long2ObjectMap<IntLinkedOpenHashSet> mergedRecipes = new Long2ObjectLinkedOpenHashMap<>();
            IntOpenHashSet restrictedTypeIndices = new IntOpenHashSet();

            for (RecipeRestrictionRawData pending : pendingData) {
                short stageId = resolveStageId(pending.stage());
                knowStagesList.add(stageId);
                int targetId = resolveTargetId(pending);
                long key = createKey(pending.registerType().getId(), targetId, stageId);
                IntLinkedOpenHashSet recipeIds = mergedRecipes.computeIfAbsent(key, ignored -> new IntLinkedOpenHashSet());

                for (ResourceLocation recipeId : pending.recipes()) {
                    int recipeIndex = managerExtern.getRecipeIndex(recipeId);
                    if (recipeIndex < 0) {
                        String errorMessage = "Unknown recipe id '" + recipeId + "'";
                        GameProgressionRecipes.LOGGER.error(errorMessage);
                        NeoForge.EVENT_BUS.post(new RegisterRestrictionErrorEvent(errorMessage));
                        return;
                    }

                    RecipeHolder<?> holder = managerExtern.getRecipeHolderByIndex(recipeIndex);
                    if (holder == null) {
                        String errorMessage = "Missing recipe holder for recipe index " + recipeIndex;
                        GameProgressionRecipes.LOGGER.error(errorMessage);
                        NeoForge.EVENT_BUS.post(new RegisterRestrictionErrorEvent(errorMessage));
                        return;
                    }

                    int recipeTypeIndex = RecipeTypeIndex.get(holder.value().getType()).gpr$getIndex();
                    restrictedTypeIndices.add(recipeTypeIndex);
                    if (pending.registerType() == RecipeRegisterType.BY_RECIPE_TYPE) {
                        recipeByStage.put(recipeIndex, stageId);
                    }
                    recipeIds.add(recipeIndex);
                }
            }

            this.recipeByStage = recipeByStage;
            this.knowStages = knowStagesList.toShortArray();
            buildRestrictions(mergedRecipes);
            applyRecipeTypesSupports(restrictedTypeIndices);

            GameProgressionRecipes.LOGGER.info("GPR: Restrictions registered. Recipes: {}, Types: {}", mergedRecipes.size(), restrictedTypeIndices.size());
        } finally {
            pendingData.clear();
        }
    }

    private void applyRecipeTypesSupports(IntOpenHashSet restrictedTypeIndices) {
        for (RecipeType<?> recipeType : BuiltInRegistries.RECIPE_TYPE) {
            if(recipeType instanceof RecipeTypeSupport support) {
                support.gpr$setHasRestrictions(
                        restrictedTypeIndices.contains(RecipeTypeIndex.get(recipeType).gpr$getIndex())
                );
            }
        }
    }

    public short[] getKnowStages() {
        return knowStages;
    }

    public short getStageByRecipe(int recipe) {
        return recipeByStage.get(recipe);
    }

    public short[] getRequiredStages(int recipeIndex, @Nullable BlockEntityType<?> blockType, @Nullable RecipeType<?> recipeType) {
        if (recipeIndex < 0 || knowStages == null || knowStages.length == 0) {
            return ShortArrays.EMPTY_ARRAY;
        }

        ShortLinkedOpenHashSet stages = new ShortLinkedOpenHashSet();

        if (recipeType != null) {
            for (short stageId : knowStages) {
                RecipeRestrictionData restriction = getRestriction(createKey(recipeType, stageId));
                if (containsRecipe(restriction, recipeIndex)) {
                    stages.add(stageId);
                }
            }
        }

        if (blockType != null) {
            for (short stageId : knowStages) {
                RecipeRestrictionData restriction = getRestriction(createKey(RecipeRegisterType.BY_BLOCK, blockType, stageId));
                if (containsRecipe(restriction, recipeIndex)) {
                    stages.add(stageId);
                }
            }
        }

        return stages.toShortArray();
    }

    public void addPendingData(RecipeRestrictionRawData data) {
        pendingData.add(data);
    }

    public @Nullable RecipeRestrictionData getRestriction(long key) {
        return restrictions.get(key);
    }

    public Long2ObjectMap<RecipeRestrictionData> getRestrictions() {
        return restrictions;
    }

    public static long createKey(RecipeRegisterType type, BlockEntity blockEntity, short stage) {
        return createKey(type, blockEntity.getType(), stage);
    }

    public static long createKey(RecipeRegisterType type, BlockEntityType<?> blockEntityType, short stage) {
        return createKey(type.getId(), BlockEntityTypeIndex.get(blockEntityType).gpr$getIndex(), stage);
    }

    public static long createKey(RecipeType<?> recipeType, short stage) {
        return createKey(RecipeRegisterType.BY_RECIPE_TYPE.getId(), RecipeTypeIndex.get(recipeType).gpr$getIndex(), stage);
    }

    public static long createKey(byte registerType, int blockId, short stage) {
        return (((long) registerType & 0xFFL) << 48) |
                (((long) stage & 0xFFFFL) << 32) |
                ((long) blockId & 0xFFFFFFFFL);
    }

    private void buildRestrictions(Long2ObjectMap<IntLinkedOpenHashSet> mergedRecipes) {
        LongArrayList sortedKeys = new LongArrayList(mergedRecipes.keySet());
        LongArrays.quickSort(sortedKeys.elements(), 0, sortedKeys.size());

        for (int i = 0; i < sortedKeys.size(); i++) {
            long key = sortedKeys.getLong(i);
            byte registerTypeId = getRegisterTypeId(key);
            short stageId = getStageId(key);
            int targetId = getTargetId(key);
            int[] recipeIds = mergedRecipes.get(key).toIntArray();

            IntArrays.quickSort(recipeIds);

            RecipeRestrictionData restriction = switch (RecipeRegisterType.values()[registerTypeId]) {
                case BY_BLOCK -> new RecipeRestrictionData(
                        RecipeRegisterType.BY_BLOCK,
                        stageId,
                        targetId,
                        RecipeRestrictionData.packData(recipeIds)
                );
                case BY_RECIPE_TYPE -> new RecipeRestrictionData(
                        RecipeRegisterType.BY_RECIPE_TYPE,
                        stageId,
                        RecipeRestrictionData.NO_BLOCK_ID,
                        RecipeRestrictionData.packData(targetId, recipeIds)
                );
            };

            restrictions.put(key, restriction);
        }
    }

    private short resolveStageId(Object rawStage) {
        if (rawStage instanceof Short stageId) {
            return stageId;
        }

        if (rawStage instanceof Integer stageId) {
            if (stageId < Short.MIN_VALUE || stageId > Short.MAX_VALUE) {
                throw new IllegalArgumentException("Stage id is out of short range: " + stageId);
            }

            return stageId.shortValue();
        }

        if (rawStage instanceof String stageName) {
            return Stages.getStageId(stageName);
        }

        throw new IllegalArgumentException("Unsupported stage object: " + rawStage);
    }

    private int resolveTargetId(RecipeRestrictionRawData pending) {
        return switch (pending.registerType()) {
            case BY_BLOCK -> {
                BlockEntityType<?> blockType = pending.block();
                if (blockType == null) {
                    throw new IllegalArgumentException("Block restriction requires a block entity type");
                }

                yield BlockEntityTypeIndex.get(blockType).gpr$getIndex();
            }
            case BY_RECIPE_TYPE -> {
                RecipeType<?> recipeType = pending.recipeType();
                if (recipeType == null) {
                    throw new IllegalArgumentException("Recipe type restriction requires a recipe type");
                }

                yield RecipeTypeIndex.get(recipeType).gpr$getIndex();
            }
        };
    }

    private static byte getRegisterTypeId(long key) {
        return (byte) ((key >>> 48) & 0xFFL);
    }

    private static short getStageId(long key) {
        return (short) ((key >>> 32) & 0xFFFFL);
    }

    private static int getTargetId(long key) {
        return (int) key;
    }

    private static boolean containsRecipe(@Nullable RecipeRestrictionData restriction, int recipeIndex) {
        if (restriction == null) {
            return false;
        }

        int[] packedData = restriction.recipeData();
        int pointer = 0;

        while (pointer < packedData.length) {
            pointer++;
            int recipeCount = packedData[pointer++];

            for (int i = 0; i < recipeCount; i++) {
                if (packedData[pointer + i] == recipeIndex) {
                    return true;
                }
            }

            pointer += recipeCount;
        }

        return false;
    }
}
