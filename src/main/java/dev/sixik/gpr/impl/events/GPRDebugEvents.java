package dev.sixik.gpr.impl.events;

import dev.sixik.gpr.api.BlockEntityOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class GPRDebugEvents {

    @SubscribeEvent
    public void onDebugInteract(PlayerInteractEvent.LeftClickBlock event) {
        BlockEntity entity = event.getLevel().getBlockEntity(event.getPos());
        if(entity == null) {
            System.out.println("null");
            return;
        }

        System.out.println(BlockEntityOwner.get(entity).gpr$getOwner() + " : " + event.getEntity().getGameProfile().getId() + " : " + event.getEntity().getUUID());
    }
}
