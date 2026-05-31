package dev.sixik.gpr.impl.mixin.data.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sixik.gpr.api.BlockEntityOwner;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BlockEntity.class)
public class MixinBlockEntity$owner_support implements BlockEntityOwner {

    @Unique
    private static final String GPR$OWNER_KEY = "gpr_owner";

    @Unique
    private UUID gpr$owner;

    @Override
    public UUID gpr$getOwner() {
        return gpr$owner;
    }

    @Override
    public void gpr$setOwner(UUID uuid) {
        this.gpr$owner = uuid;
    }

    @Override
    public void gpr$setOwnerAndSync(UUID uuid) {
        this.gpr$owner = uuid;
        BlockEntity self = (BlockEntity) (Object) this;
        self.setChanged();

        if(self.getLevel() == null) {
            return;
        }

        BlockState state = self.getBlockState();
        self.getLevel().sendBlockUpdated(self.getBlockPos(), state, state, 3);
    }

    @Unique
    private void gpr$writeOwner(CompoundTag nbt) {
        nbt.putString(GPR$OWNER_KEY, gpr$owner == null ? "" : gpr$owner.toString());
    }

    @Inject(method = {"saveWithoutMetadata", "saveCustomOnly"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    public void gpr$saveWithoutMetadata(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir, @Local CompoundTag nbt) {
        gpr$writeOwner(nbt);
    }

    @Inject(method = {"loadWithComponents", "loadCustomOnly"}, at = @At("HEAD"))
    public void gpr$loadWithComponents(CompoundTag nbt, HolderLookup.Provider unused, CallbackInfo ci) {
        if(nbt.contains(GPR$OWNER_KEY)) {
            String string_id = nbt.getString(GPR$OWNER_KEY);
            if(string_id.isEmpty()) {
                gpr$owner = null;
                return;
            }

            gpr$owner = UUID.fromString(string_id);
        }
    }

    @Inject(method = "getUpdateTag", at = @At("RETURN"))
    public void gpr$getUpdateTag(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        gpr$writeOwner(cir.getReturnValue());
    }

    @Inject(method = "getUpdatePacket", at = @At("HEAD"), cancellable = true)
    public void gpr$getUpdatePacket(CallbackInfoReturnable<ClientboundBlockEntityDataPacket> cir) {
        BlockEntity self = (BlockEntity) (Object) this;
        cir.setReturnValue(ClientboundBlockEntityDataPacket.create(self));
    }
}
