package dev.sixik.gpr.impl.mixin.data.gpf;

import dev.sixik.gpf.data.BaseBitStageData;
import dev.sixik.gpf.impl.client.ClientStageData;
import dev.sixik.gpr.impl.events.GPRClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientStageData.class)
public class MixinClientStageData$sync_data_hook {

    @Shadow
    public static BaseBitStageData INSTANCE;

    @Inject(method = "applySync", at = @At("RETURN"))
    private static void gpr$applySync(UUID ownerId, long[] rawStages, CallbackInfo ci) {
        GPRClientEvents.onStageSyncEvent(ownerId, rawStages, INSTANCE);
    }
}
