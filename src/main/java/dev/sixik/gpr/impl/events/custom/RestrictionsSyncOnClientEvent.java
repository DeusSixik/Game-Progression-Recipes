package dev.sixik.gpr.impl.events.custom;

import dev.sixik.gpr.impl.registry.RecipeRestrictionData;
import net.neoforged.bus.api.Event;

import java.util.List;

public class RestrictionsSyncOnClientEvent extends Event {

    private final List<RecipeRestrictionData> restrictions;

    public RestrictionsSyncOnClientEvent(List<RecipeRestrictionData> restrictions) {
        this.restrictions = restrictions;
    }

    public List<RecipeRestrictionData> getRestrictions() {
        return restrictions;
    }
}
