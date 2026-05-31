package dev.sixik.gpr.impl.compat.jei;

import dev.sixik.gpr.GameProgressionRecipes;
import dev.sixik.gpr.impl.compat.jei.hider_pipeline.JeiRecipeHideEntrypoint;
import dev.sixik.gpr.impl.events.custom.RestrictionsSyncOnClientEvent;
import dev.sixik.gpr.impl.events.custom.StageSyncOnClientEvent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class GPRJeiPlugin implements IModPlugin {

    private static IJeiRuntime runtime;

    private boolean isReloading;

    @Override
    @SuppressWarnings("all")
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.tryBuild(GameProgressionRecipes.MODID, "jei_plugin");
    }

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        GPRJeiPlugin.runtime = jeiRuntime;
        NeoForge.EVENT_BUS.addListener(this::onStageSync);
        NeoForge.EVENT_BUS.addListener(this::onRestrictionSync);
        startReloading();
    }

    public void onStageSync(StageSyncOnClientEvent event) {
        startReloading();
    }

    public void onRestrictionSync(RestrictionsSyncOnClientEvent event) {
        startReloading();
    }

    public final void startReloading() {
        if(isReloading) return;
        Minecraft.getInstance().submit(() -> {
            if(isReloading) return;
            isReloading = true;
            onReload();

            isReloading = false;
        });
    }

    protected void onReload() {
        JeiRecipeHideEntrypoint.invoke(runtime);
    }
}
