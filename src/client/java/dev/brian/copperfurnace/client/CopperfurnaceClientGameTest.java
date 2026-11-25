package dev.brian.copperfurnace.client;

import dev.brian.copperfurnace.Copperfurnace;
import dev.brian.copperfurnace.ModBlocks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.state.BlockState;

/**
 * In-game test: places a copper furnace next to a vanilla furnace, smelts the same input in both and
 * asserts the copper one finishes first; then checks that weathering, axe scraping and waxing keep the
 * furnace contents. Screenshots land in {@code build\run\clientGameTest\screenshots}.
 */
public class CopperfurnaceClientGameTest implements FabricClientGameTest {
	private static final BlockPos COPPER = new BlockPos(2, 101, 6);
	private static final BlockPos VANILLA = new BlockPos(4, 101, 6);

	@Override
	public void runTest(ClientGameTestContext context) {
		expect(FabricLoader.getInstance().isModLoaded(Copperfurnace.MOD_ID), "mod not loaded");
		context.waitForScreen(TitleScreen.class);
		context.waitTicks(60);

		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			for (String path : new String[] {"copper_furnace", "exposed_copper_furnace", "weathered_copper_furnace",
					"oxidized_copper_furnace", "waxed_copper_furnace", "waxed_exposed_copper_furnace",
					"waxed_weathered_copper_furnace", "waxed_oxidized_copper_furnace"}) {
				expect(BuiltInRegistries.BLOCK.containsKey(Copperfurnace.id(path)), "block not registered: " + path);
				expect(BuiltInRegistries.ITEM.containsKey(Copperfurnace.id(path)), "item not registered: " + path);
			}
			boolean recipe = world.getServer().computeOnServer(server -> server.getRecipeManager()
				.byKey(ResourceKey.create(Registries.RECIPE, Copperfurnace.id("copper_furnace"))).isPresent());
			expect(recipe, "recipe copperfurnace:copper_furnace missing");
			pass("registration and recipe");

			world.getServer().runCommand("gamerule random_tick_speed 0");
			world.getServer().runCommand("fill 0 100 0 8 100 8 stone");
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			world.getServer().runCommand("tp @p 3.5 101 2.5 0 0");
			world.getServer().runCommand("setblock 2 101 6 copperfurnace:copper_furnace[facing=north]");
			world.getServer().runCommand("setblock 4 101 6 minecraft:furnace[facing=north]");
			for (BlockPos pos : new BlockPos[] {COPPER, VANILLA}) {
				String at = pos.getX() + " " + pos.getY() + " " + pos.getZ();
				world.getServer().runCommand("item replace block " + at + " container.0 with minecraft:raw_iron 3");
				world.getServer().runCommand("item replace block " + at + " container.1 with minecraft:coal 1");
			}
			// 134 ticks per item for the copper furnace, 200 for vanilla. Check at ~170 ticks.
			context.waitTicks(170);
			int copperOut = output(world, COPPER);
			int vanillaOut = output(world, VANILLA);
			expect(copperOut == 1 && vanillaOut == 0,
				"after 170 ticks expected copper=1 vanilla=0, got copper=" + copperOut + " vanilla=" + vanillaOut);
			context.takeScreenshot("smelting_side_by_side");
			pass("copper furnace smelts faster (" + copperOut + " vs " + vanillaOut + ")");

			// Stage change (what random-tick weathering does) keeps the block entity and its items.
			world.getServer().runOnServer(server -> {
				ServerLevel level = server.overworld();
				BlockState state = level.getBlockState(COPPER);
				level.setBlockAndUpdate(COPPER, ModBlocks.EXPOSED_COPPER_FURNACE.withPropertiesOf(state));
			});
			context.waitTicks(2);
			expect(blockId(world, COPPER).equals("copperfurnace:exposed_copper_furnace"), "did not weather");
			expect(output(world, COPPER) >= 1, "weathering lost the output stack");
			expect(input(world, COPPER) >= 1, "weathering lost the input stack");
			pass("weathering keeps contents");

			// Survival: sneak + axe scrapes one stage, honeycomb waxes. Contents must survive both.
			world.getServer().runCommand("gamemode survival @p");
			context.getInput().lookAt(COPPER);
			give(world, "minecraft:iron_axe");
			sneakUse(context);
			expect(blockId(world, COPPER).equals("copperfurnace:copper_furnace"),
				"axe did not scrape, block is " + blockId(world, COPPER));
			give(world, "minecraft:honeycomb");
			sneakUse(context);
			expect(blockId(world, COPPER).equals("copperfurnace:waxed_copper_furnace"),
				"honeycomb did not wax, block is " + blockId(world, COPPER));
			expect(output(world, COPPER) >= 1, "scrape/wax lost the output stack");
			give(world, "minecraft:iron_axe");
			sneakUse(context);
			expect(blockId(world, COPPER).equals("copperfurnace:copper_furnace"),
				"axe did not remove wax, block is " + blockId(world, COPPER));
			pass("scrape, wax and unwax keep contents");

			// Screenshot: all four stages, alternating lit.
			world.getServer().runCommand("gamemode creative @p");
			world.getServer().runCommand("fill 0 101 6 8 101 6 air");
			String[] stages = {"", "exposed_", "weathered_", "oxidized_"};
			for (int i = 0; i < 4; i++) {
				world.getServer().runCommand("setblock " + (1 + 2 * i) + " 101 6 copperfurnace:" + stages[i]
					+ "copper_furnace[facing=north,lit=" + (i % 2 == 0) + "]");
			}
			world.getServer().runCommand("tp @p 4.5 102 1.5 0 20");
			context.waitTicks(20);
			context.takeScreenshot("four_stages");
			pass("screenshot");
		}
	}

	private static void sneakUse(ClientGameTestContext context) {
		context.getInput().holdKey(options -> options.keyShift);
		context.waitTicks(3);
		context.getInput().pressKey(options -> options.keyUse);
		context.waitTicks(5);
		context.getInput().releaseKey(options -> options.keyShift);
		context.waitTicks(3);
	}

	private static void give(TestSingleplayerContext world, String stack) {
		world.getServer().runCommand("item replace entity @p weapon.mainhand with " + stack);
	}

	private static String blockId(TestSingleplayerContext world, BlockPos pos) {
		return world.getServer().computeOnServer(server ->
			BuiltInRegistries.BLOCK.getKey(server.overworld().getBlockState(pos).getBlock()).toString());
	}

	private static int slotCount(TestSingleplayerContext world, BlockPos pos, int slot) {
		return world.getServer().computeOnServer(server ->
			server.overworld().getBlockEntity(pos) instanceof Container c ? c.getItem(slot).getCount() : -1);
	}

	private static int input(TestSingleplayerContext world, BlockPos pos) {
		return slotCount(world, pos, 0);
	}

	private static int output(TestSingleplayerContext world, BlockPos pos) {
		return slotCount(world, pos, 2);
	}

	private static void pass(String step) {
		System.out.println("[copperfurnace-gametest] PASS " + step);
	}

	private static void expect(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
