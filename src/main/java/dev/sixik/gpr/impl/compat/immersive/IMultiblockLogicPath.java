package dev.sixik.gpr.impl.compat.immersive;

import java.util.UUID;

public interface IMultiblockLogicPath {

    default void gpr$setOwner(UUID owner) { }

    default UUID gpr$getOwner() {
        return null;
    }
}
