package dev.sixik.gpr.impl.events;

import dev.sixik.gpf.data.BaseBitStageData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class GPRClientEvents {

    public static void onStageSyncEvent(UUID ownerId, long[] rawStages, BaseBitStageData data) {

    }
}
