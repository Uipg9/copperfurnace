package dev.brian.copperfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A smelting-only furnace that uses vanilla's fuel speed multiplier hook: cook time is divided by the
 * stage multiplier and fuel burn time scaled down so one fuel item smelts the vanilla item count.
 * One block entity type covers all 8 blocks, so oxidizing, scraping and waxing keep the contents.
 */
public class CopperFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public CopperFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.COPPER_FURNACE_ENTITY, pos, state, RecipeType.SMELTING);
	}

	private WeatherState stage() {
		return getBlockState().getBlock() instanceof CopperFurnaceBlock furnace ? furnace.stage() : WeatherState.OXIDIZED;
	}

	@Override
	protected float getSpeedMultiplier(ServerLevel level, ItemStack fuel) {
		return super.getSpeedMultiplier(level, fuel) * CookTime.multiplier(stage());
	}

	@Override
	protected int getBurnDuration(ServerLevel level, ItemStack fuel) {
		return CookTime.burnForStage(stage(), super.getBurnDuration(level, fuel));
	}

	@Override
	protected Component getDefaultName() {
		return getBlockState().getBlock().getName();
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new FurnaceMenu(containerId, inventory, this, dataAccess);
	}
}
