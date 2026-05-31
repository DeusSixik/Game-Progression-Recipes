package dev.sixik.gpr.impl.mixin.recipes.mods.immersive_engineering.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AssemblerLogic;
import dev.sixik.gpr.impl.compat.immersive.IMultiblockStatePath;
import org.spongepowered.asm.mixin.Mixin;

// TODO: I dont know what do this
@Deprecated
@Mixin(AssemblerLogic.State.class)
public abstract class MixinAssemblerLogic$State implements IMultiblockState, IMultiblockStatePath {
}
