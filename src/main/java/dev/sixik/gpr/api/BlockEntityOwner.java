package dev.sixik.gpr.api;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BlockEntityOwner {

    static BlockEntityOwner get(BlockEntity entity) {
        if(entity instanceof BlockEntityOwner owner) {
            return owner;
        }

        return new Fallback();
    }

    @Nullable
    UUID gpr$getOwner();

    void gpr$setOwner(UUID uuid);

    default void gpr$setOwnerAndSync(UUID uuid) {
        gpr$setOwner(uuid);
    }

    final class Fallback implements BlockEntityOwner {

        @Override
        @Nullable
        public UUID gpr$getOwner() {
            return null;
        }

        @Override
        public void gpr$setOwner(UUID uuid) { }
    }
}
