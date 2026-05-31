package dev.sixik.gpr.impl.events.custom;


import dev.sixik.gpf.data.BaseBitStageData;
import net.neoforged.bus.api.Event;

import java.util.UUID;

public class StageSyncOnClientEvent extends Event {

   public final UUID ownerId;
   public final long[] rawStages;
   public final BaseBitStageData data;

    public StageSyncOnClientEvent(UUID ownerId, long[] rawStages, BaseBitStageData data) {
        this.ownerId = ownerId;
        this.rawStages = rawStages;
        this.data = data;
    }
}
