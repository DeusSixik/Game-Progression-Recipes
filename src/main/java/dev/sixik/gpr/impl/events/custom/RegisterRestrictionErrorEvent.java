package dev.sixik.gpr.impl.events.custom;

import net.neoforged.bus.api.Event;

public class RegisterRestrictionErrorEvent extends Event {

    private final String message;

    public RegisterRestrictionErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
