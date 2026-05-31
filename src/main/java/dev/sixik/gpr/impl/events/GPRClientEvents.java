package dev.sixik.gpr.impl.events;

import dev.sixik.gpf.data.BaseBitStageData;
import dev.sixik.gpr.impl.events.custom.RestrictionsSyncOnClientEvent;
import dev.sixik.gpr.impl.events.custom.StageSyncOnClientEvent;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import dev.sixik.gpr.impl.registry.RecipeRestrictionData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class GPRClientEvents {

    public static GPRRegistry CLIENT_DATA = new GPRRegistry();

    public static void onStageSyncEvent(UUID ownerId, long[] rawStages, BaseBitStageData data) {
        NeoForge.EVENT_BUS.post(new StageSyncOnClientEvent(ownerId, rawStages, data));
    }

    public static void onRestrictionsSyncEvent(List<RecipeRestrictionData> restrictions) {
        CLIENT_DATA.applySyncedRestrictions(restrictions);
        NeoForge.EVENT_BUS.post(new RestrictionsSyncOnClientEvent(restrictions));
    }
}
