# Copper Furnace: working notes for Claude

- Minecraft 26.3 Fabric mod. Java 25. Unobfuscated: use Mojang names, never add mappings/yarn.
- TDD: write a failing JUnit test in `src/test` first, then implement. In-game checks go in
  `src/client/.../*ClientGameTest.java`.
- Shell is Windows PowerShell: use `.\gradlew.bat`. Filter Gradle output to errors only.
- Verify with `.\scripts\verify.ps1` (build + unit tests + client gametest). Read the gametest
  screenshots in `build\run\clientGameTest\screenshots\` to check what actually rendered.
- Never kill processes to stop the client: `runClientGameTest` exits by itself.
- Rendering: PocketCanvasCore (`new PocketCanvas(PocketRenderContexts.of(graphics, deltaTracker))`,
  then `drawRect/drawRoundedRect/drawCircle` with `PocketTheme` colors). See `ExampleCanvasScreen`.
- UI: PocketUICore (`PocketScreen`, `VerticalListPanel`, `GridPanel`, `HoverButton`); see `ExamplePocketScreen`.
  26.3 differences: use `PocketKeys.*` not GLFW constants, left mouse button = 1, open screens with
  `Minecraft.getInstance().gui.setScreen(...)`. Gamepad (SDL) is only tested against a fake backend.
- Mod Menu is dev-only (`localRuntime`); do not make it a hard dependency.
