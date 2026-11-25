# Copper Furnace roadmap

## Done
- 8 blocks (4 stages x waxed/unwaxed), one shared block entity type, BlockItems, creative tab
  (Functional Blocks, after the vanilla furnace), lang, blockstates/models (vanilla `orientable`, lit
  variant), loot tables (drops itself), `mineable/pickaxe` tag, recipe (furnace + 8 copper ingots).
- Speed: `CookTime` (pure, unit-tested). Uses the 26.3 vanilla fuel-speed hook
  `AbstractFurnaceBlockEntity.getSpeedMultiplier`, which sets cook time to `ceil(ticks / multiplier)`
  (so 200 -> 134, not 133). `getBurnDuration` is scaled so each fuel smelts the vanilla item count.
- Smelting only (`RecipeType.SMELTING`), vanilla `FurnaceMenu`/`FurnaceScreen`.
- Oxidation via Fabric `OxidizableBlocksRegistry` (next stage + waxable); random ticks only on unwaxed.

- Contents survive stage changes: `shouldChangedStateKeepBlockEntity` returns true for any copper furnace.
- Verified by verify.ps1 (VERIFY OK): unit tests; client gametest smelts raw iron side by side (copper 1
  vs vanilla 0 after 170 ticks), weathering keeps items, sneak+axe scrape, honeycomb wax, axe unwax;
  screenshots show all 4 stage textures, lit and unlit.

## Untested / unverified
- Random-tick weathering itself (only the resulting setBlock was tested), fuel-count equality in game
  (unit-tested only), creative tab placement, crafting the recipe in a crafting table (recipe loads).
- Hopper interaction, comparator output, XP from the result slot (inherited from vanilla, not tested).
- Speed change mid-burn: the multiplier is captured when fuel is consumed, so a stage change applies
  from the next fuel item (vanilla behaviour of the hook).

## Not done
- Plain right-click with an axe/honeycomb opens the GUI (furnace block action wins); you must sneak.
  Vanilla copper chests behave the same way.
- Scrape/wax particles and sounds come from vanilla AxeItem/HoneycombItem (should work, unchecked).
- No GitHub repo / CI run yet.
