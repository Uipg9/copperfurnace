package dev.brian.copperfurnace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import org.junit.jupiter.api.Test;

class CookTimeTest {
	@Test
	void multipliersPerStage() {
		assertEquals(1.5f, CookTime.multiplier(WeatherState.UNAFFECTED));
		assertEquals(1.35f, CookTime.multiplier(WeatherState.EXPOSED));
		assertEquals(1.2f, CookTime.multiplier(WeatherState.WEATHERED));
		assertEquals(1.0f, CookTime.multiplier(WeatherState.OXIDIZED));
	}

	@Test
	void cookTimeRoundsUpLikeVanilla() {
		assertEquals(134, CookTime.forStage(WeatherState.UNAFFECTED, 200));
		assertEquals(149, CookTime.forStage(WeatherState.EXPOSED, 200));
		assertEquals(167, CookTime.forStage(WeatherState.WEATHERED, 200));
		assertEquals(200, CookTime.forStage(WeatherState.OXIDIZED, 200));
	}

	@Test
	void fuelSmeltsTheSameNumberOfItems() {
		for (WeatherState stage : WeatherState.values()) {
			int cook = CookTime.forStage(stage, 200);
			assertEquals(8 * cook, CookTime.burnForStage(stage, 1600), "coal at " + stage);
			assertEquals(100 * cook, CookTime.burnForStage(stage, 20000), "lava at " + stage);
		}
		assertEquals(1600, CookTime.burnForStage(WeatherState.OXIDIZED, 1600));
		assertEquals(0, CookTime.burnForStage(WeatherState.UNAFFECTED, 0));
	}
}
