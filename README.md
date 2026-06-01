# Game Progression Recipes

Game Progression Recipes (GPR) is a NeoForge 1.21.1 addon for [Game Progression Framework](https://github.com/DeusSixik/Game-Progression-Framework).

It hides and blocks recipes until the required stage is unlocked, and it also applies the same progression rules to machines by tracking the owner of placed block entities.

## Features

- Stage-based recipe restrictions built on top of GPF.
- Deterministic recipe indexing for fast runtime lookups.
- Client sync for recipe restrictions.
- JEI visibility updates when stages or restrictions change.
- Block entity owner tracking, so machines use the stages of the player who placed them.
- Script integrations for CraftTweaker and KubeJS.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- Game Progression Framework `1.0.3+`

## How it works

GPR stores recipe restrictions in a compact indexed format.

At runtime:

- recipes are resolved to deterministic integer indices;
- restrictions are merged by target and stage;
- the server validates access with fast stage checks;
- the client receives synced restriction data for UI features such as JEI hiding.

For machines and other block entities, GPR stores an owner UUID when the block is placed. Recipe access is then checked against that owner's unlocked stages.

## Registration lifecycle

GPR depends on finalized stage ids from GPF.

Use this split:

- `StageRegisterEvent` - declare stage names only.
- `StageRegisterFinalizeEvent` - register recipe restrictions.

Do not register recipe restrictions before stage finalization.

## Java API

Main entry point:

- `src/main/java/dev/sixik/gpr/api/RecipeStages.java`

### Register recipe restrictions

```java
import dev.sixik.gpr.api.RecipeStages;
import net.minecraft.world.item.crafting.RecipeType;

RecipeStages.addRecipe(
    "bronze_age",
    RecipeType.CRAFTING,
    new String[] {
        "minecraft:iron_pickaxe",
        "minecraft:iron_axe"
    }
);
```

### String recipe type overload

```java
RecipeStages.addRecipe(
    "bronze_age",
    "minecraft:crafting",
    new String[] {
        "minecraft:iron_pickaxe"
    }
);
```

### Validation

The public API validates inputs early and throws readable errors for:

- invalid recipe ids;
- invalid recipe type ids;
- unknown recipe types;
- null recipe types.

## CraftTweaker

Class:

- `mods.gpr.api.RecipeStage`

Example:

```zenscript
import mods.gpr.api.RecipeStage;

events.register<mods.gpf.api.events.StageRegisterEndEvent>(event => {
    RecipeStage.addRecipe("bronze_age", "minecraft:crafting", [
        "minecraft:iron_pickaxe",
        "minecraft:iron_axe"
    ]);
});
```

You can also pass a CraftTweaker recipe manager instead of a string recipe type.

## KubeJS

Binding:

- `GPRRecipes`

Example:

```js
GPFEvents.stageRegisterEnd(event => {
  GPRRecipes.addRecipe('bronze_age', 'minecraft:crafting', [
    'minecraft:iron_pickaxe',
    'minecraft:iron_axe'
  ])
})
```

KubeJS uses string recipe type ids because it does not expose Minecraft `RecipeType` objects directly in the same way as Java.

## JEI integration

If JEI is installed, GPR updates recipe visibility on the client using synced restriction data.

This includes:

- initial runtime sync;
- stage updates;
- datapack and `/reload` restriction rebuilds.

## Notes

- GPR is designed around fast hot-path checks.
- Restriction data is merged to reduce duplicate runtime structures.
- Block-based restrictions and recipe-type restrictions both resolve to compact indexed data internally.

## Summary

Use GPR when you want recipe progression that is:

- deterministic;
- server-authoritative;
- synchronized to the client;
- script-friendly for modpacks.
