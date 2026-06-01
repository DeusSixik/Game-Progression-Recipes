package dev.sixik.gpr.impl.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public final class GPRKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        if (bindings.type().isServer()) {
            bindings.add("GPRRecipes", GPRKubeJS.class);
        }
    }
}
