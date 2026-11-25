package dev.brian.copperfurnace;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

/**
 * Speed math for the copper furnace. Pure, so it is unit-tested without a world.
 * Cook time rounds up exactly like vanilla's fuel speed multiplier ({@code ceil(ticks / multiplier)}),
 * and fuel burns proportionally faster so each fuel item still smelts the vanilla number of items.
 */
public final class CookTime {
	/** Vanilla smelting time, used as the reference for scaling fuel. */
	public static final int VANILLA_COOK_TICKS = 200;

	private CookTime() {
	}

	public static float multiplier(WeatherState stage) {
		return switch (stage) {
			case UNAFFECTED -> 1.5f;
			case EXPOSED -> 1.35f;
			case WEATHERED -> 1.2f;
			case OXIDIZED -> 1.0f;
		};
	}

	public static int forStage(WeatherState stage, int vanillaTicks) {
		return (int) Math.ceil(vanillaTicks / multiplier(stage));
	}

	/** Burn ticks such that {@code burn / forStage(stage, 200) == vanillaBurn / 200}. */
	public static int burnForStage(WeatherState stage, int vanillaBurn) {
		return (int) Math.ceil((double) vanillaBurn * forStage(stage, VANILLA_COOK_TICKS) / VANILLA_COOK_TICKS);
	}
}
