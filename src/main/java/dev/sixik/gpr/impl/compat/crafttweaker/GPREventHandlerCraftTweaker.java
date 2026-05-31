package dev.sixik.gpr.impl.compat.crafttweaker;


import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.impl.events.custom.RegisterRestrictionErrorEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class GPREventHandlerCraftTweaker {

    @SubscribeEvent
    public void onErrorMessage(RegisterRestrictionErrorEvent event) {
        CraftTweakerAPI.getLogger(GameProgressionRecipes.MODID).error(event.getMessage());
    }
}
