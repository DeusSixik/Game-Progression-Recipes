package dev.sixik.gpr.impl.mixin.data.vanilla;

import dev.sixik.gpr.api.BlockEntityTypeIndex;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityType.class)
public class MixinBlockEntityType implements BlockEntityTypeIndex {

    @Unique
    private int gpr$index = -1;

    @Override
    public int gpr$getIndex() {
        return gpr$index;
    }

    @Override
    public void gpr$setIndex(int index) {
        this.gpr$index = index;
    }
}
