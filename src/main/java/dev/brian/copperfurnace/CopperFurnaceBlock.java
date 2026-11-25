package dev.brian.copperfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copper furnace at one oxidation stage. Extends the vanilla furnace for facing, lit state, light,
 * particles and sounds. Weathering (random ticks) only happens on unwaxed blocks; the next/previous
 * stages and wax pairs are registered with Fabric's {@code OxidizableBlocksRegistry}.
 */
public class CopperFurnaceBlock extends FurnaceBlock implements WeatheringCopper {
	private final WeatherState stage;
	private final boolean waxed;

	public CopperFurnaceBlock(WeatherState stage, boolean waxed, Properties properties) {
		super(properties);
		this.stage = stage;
		this.waxed = waxed;
	}

	public WeatherState stage() {
		return stage;
	}

	public boolean waxed() {
		return waxed;
	}

	@Override
	public WeatherState getAge() {
		return stage;
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return !waxed && WeatheringCopper.getNext(state.getBlock()).isPresent();
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!waxed) {
			changeOverTime(state, level, pos, random);
		}
	}

	/** Weathering, scraping and waxing swap the block; keep the block entity (items, progress) across them. */
	@Override
	protected boolean shouldChangedStateKeepBlockEntity(BlockState newState) {
		return newState.getBlock() instanceof CopperFurnaceBlock;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CopperFurnaceBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createFurnaceTicker(level, type, ModBlocks.COPPER_FURNACE_ENTITY);
	}

	@Override
	protected void openContainer(Level level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof CopperFurnaceBlockEntity furnace) {
			player.openMenu((MenuProvider) furnace);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
	}
}
