package dev.sixik.gpr.impl.mixin.recipes.mods.applied_energistics;

import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberRecipe;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.utils.RecipeUnblockOwnerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InscriberBlockEntity.class)
public abstract class MixinInscriberBlockEntity extends AENetworkedPoweredBlockEntity {

    private MixinInscriberBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "getTask", at = @At("HEAD"))
    public void bts$getTask(CallbackInfoReturnable<InscriberRecipe> cir) {
        RecipeUnblockOwnerUtils.setOwner(BlockEntityOwner.get(this).gpr$getOwner());
    }
}
