package dev.sixik.gpr.impl.events;

import dev.sixik.gpf.api.event.StageRegisterEvent;
import dev.sixik.gpf.api.event.StageRegisterFinalizeEvent;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.registry.GPRRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class GPREvents {

    @SubscribeEvent
    public static void onStageRegisterEvent(StageRegisterEvent event) {
        GPRRegistry.INSTANCE.clearData();
    }

    @SubscribeEvent
    public static void onStageRegisterFinalizeEvent(StageRegisterFinalizeEvent event) {
        GPRRegistry.INSTANCE.registerData();
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        BlockEntity entity = event.getLevel().getBlockEntity(event.getPos());
        if(entity == null) {
            return;
        }

        BlockEntityOwner.get(entity).gpr$setOwnerAndSync(player.getUUID());
    }

}
