package dev.sixik.gpr;

import com.mojang.logging.LogUtils;
import dev.sixik.gpr.impl.events.GPRDebugEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(GameProgressionRecipes.MODID)
public class GameProgressionRecipes
{
    public static final String MODID = "game_progression_recipes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GameProgressionRecipes(IEventBus modEventBus, ModContainer modContainer) {

        if(!FMLEnvironment.production) {
            NeoForge.EVENT_BUS.register(new GPRDebugEvents());
        }

        if(ModList.get().isLoaded("crafttweaker")) {
            NeoForge.EVENT_BUS.register(new dev.sixik.gpr.impl.compat.crafttweaker.GPREventHandlerCraftTweaker());
        }
    }
}
