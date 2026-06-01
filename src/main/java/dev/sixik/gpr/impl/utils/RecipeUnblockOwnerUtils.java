package dev.sixik.gpr.impl.utils;

import java.util.UUID;

public class RecipeUnblockOwnerUtils {

    private static ThreadLocal<UUID> owner = new ThreadLocal<>();

    public static UUID getOwner() {
        return owner.get();
    }

    public static void setOwner(UUID owner) {
        RecipeUnblockOwnerUtils.owner.set(owner);
    }
}
