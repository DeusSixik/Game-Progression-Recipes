package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocked;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import dev.sixik.gpr.impl.compat.immersive.MultiblockPlacerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemplateMultiblock.class)
public class MixinTemplateMultiblock {

    @Inject(method = "createStructure", at = @At(value = "INVOKE", target = "Lblusunrize/immersiveengineering/api/multiblocks/TemplateMultiblock;form(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/core/Direction;)V"))
    public void gpa$createStructure(Level world, BlockPos pos, Direction side, Player player, CallbackInfoReturnable<Boolean> cir) {
        MultiblockPlacerContext.player = player;
    }
}
