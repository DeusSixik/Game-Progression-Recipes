package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocked;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperMaster;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockLogicPath;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.BiConsumer;

@Mixin(MultiblockBEHelperMaster.class)
public abstract class MixinMultiblockBEHelperMaster<State extends IMultiblockState>
        extends MultiblockBEHelperCommon<State>
        implements IMultiblockBEHelperMaster<State>, BlockEntityOwner {

    @Shadow
    @Final
    private State state;
    @Unique
    private static final String GPR$OWNER_KEY = "gpr_owner";

    @Unique
    private UUID gpr$owner;

    private MixinMultiblockBEHelperMaster(BlockEntity be, MultiblockRegistration<State> multiblock, BlockState state) {
        super(be, multiblock, state);
    }

    @Inject(method = "save", at = @At("RETURN"))
    public void gpr$save(CompoundTag nbt, BiConsumer<IMultiblockState, CompoundTag> saveSingle, CallbackInfo ci) {
        nbt.putString(GPR$OWNER_KEY, gpr$owner == null ? "" : gpr$owner.toString());
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;Ljava/util/function/BiConsumer;)V", at = @At("RETURN"))
    public void gpr$load(CompoundTag nbt, BiConsumer<IMultiblockState, CompoundTag> saveSingle, CallbackInfo ci) {
        if(nbt.contains(GPR$OWNER_KEY)) {
            String string_id = nbt.getString(GPR$OWNER_KEY);
            if(string_id.isEmpty()) {
                gpr$owner = null;
                return;
            }

            gpr$setOwner(UUID.fromString(string_id));
        }
    }

    @Override
    public void gpr$setOwner(UUID uuid) {
        this.gpr$owner = uuid;

        if(state instanceof IMultiblockStatePath path) {
            path.gpr$setOwner(gpr$owner);
        }

        if(multiblock.logic() instanceof IMultiblockLogicPath logic) {
            logic.gpr$setOwner(gpr$owner);
        }
    }

    @Override
    public @Nullable UUID gpr$getOwner() {
        return gpr$owner;
    }
}
