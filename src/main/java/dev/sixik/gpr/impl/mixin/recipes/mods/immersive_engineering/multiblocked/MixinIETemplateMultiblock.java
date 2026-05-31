package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocked;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sixik.gpr.api.BlockEntityOwner;
import dev.sixik.gpr.impl.compat.immersive.MultiblockPlacerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IETemplateMultiblock.class)
public abstract class MixinIETemplateMultiblock {

    @Inject(method = "replaceStructureBlock", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/multiblocks/blocks/logic/IMultiblockBE;getHelper()Lblusunrize/immersiveengineering/api/multiblocks/blocks/env/IMultiblockBEHelper;"))
    public void gpr$replaceStructureBlock(
            StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored,
            Direction clickDirection, Vec3i offsetFromMaster, CallbackInfo ci, @Local(type = BlockEntity.class, ordinal = 0) BlockEntity curr
    ) {
        BlockEntityOwner.get(curr).gpr$setOwner(MultiblockPlacerContext.player.getGameProfile().getId());
        IMultiblockBEHelper<IMultiblockState> helper = ((IMultiblockBE<IMultiblockState>)curr).getHelper();
        if(helper instanceof BlockEntityOwner owner) {
            owner.gpr$setOwner(MultiblockPlacerContext.player.getGameProfile().getId());
        }
    }
}
