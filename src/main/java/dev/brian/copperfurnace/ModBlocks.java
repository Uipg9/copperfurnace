package dev.brian.copperfurnace;

import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	public static final CopperFurnaceBlock COPPER_FURNACE = register("copper_furnace", WeatherState.UNAFFECTED, false);
	public static final CopperFurnaceBlock EXPOSED_COPPER_FURNACE = register("exposed_copper_furnace", WeatherState.EXPOSED, false);
	public static final CopperFurnaceBlock WEATHERED_COPPER_FURNACE = register("weathered_copper_furnace", WeatherState.WEATHERED, false);
	public static final CopperFurnaceBlock OXIDIZED_COPPER_FURNACE = register("oxidized_copper_furnace", WeatherState.OXIDIZED, false);
	public static final CopperFurnaceBlock WAXED_COPPER_FURNACE = register("waxed_copper_furnace", WeatherState.UNAFFECTED, true);
	public static final CopperFurnaceBlock WAXED_EXPOSED_COPPER_FURNACE = register("waxed_exposed_copper_furnace", WeatherState.EXPOSED, true);
	public static final CopperFurnaceBlock WAXED_WEATHERED_COPPER_FURNACE = register("waxed_weathered_copper_furnace", WeatherState.WEATHERED, true);
	public static final CopperFurnaceBlock WAXED_OXIDIZED_COPPER_FURNACE = register("waxed_oxidized_copper_furnace", WeatherState.OXIDIZED, true);

	public static final List<CopperFurnaceBlock> UNWAXED = List.of(
		COPPER_FURNACE, EXPOSED_COPPER_FURNACE, WEATHERED_COPPER_FURNACE, OXIDIZED_COPPER_FURNACE);
	public static final List<CopperFurnaceBlock> WAXED = List.of(
		WAXED_COPPER_FURNACE, WAXED_EXPOSED_COPPER_FURNACE, WAXED_WEATHERED_COPPER_FURNACE, WAXED_OXIDIZED_COPPER_FURNACE);

	public static final BlockEntityType<CopperFurnaceBlockEntity> COPPER_FURNACE_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Copperfurnace.id("copper_furnace"),
		new BlockEntityType<>(CopperFurnaceBlockEntity::new, Set.<Block>of(
			COPPER_FURNACE, EXPOSED_COPPER_FURNACE, WEATHERED_COPPER_FURNACE, OXIDIZED_COPPER_FURNACE,
			WAXED_COPPER_FURNACE, WAXED_EXPOSED_COPPER_FURNACE, WAXED_WEATHERED_COPPER_FURNACE, WAXED_OXIDIZED_COPPER_FURNACE)));

	private ModBlocks() {
	}

	private static CopperFurnaceBlock register(String path, WeatherState stage, boolean waxed) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Copperfurnace.id(path));
		BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
			.mapColor(switch (stage) {
				case UNAFFECTED -> MapColor.COLOR_ORANGE;
				case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
				case WEATHERED -> MapColor.WARPED_STEM;
				case OXIDIZED -> MapColor.WARPED_NYLIUM;
			})
			.setId(key);
		CopperFurnaceBlock block = Registry.register(BuiltInRegistries.BLOCK, key, new CopperFurnaceBlock(stage, waxed, properties));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Copperfurnace.id(path));
		Registry.register(BuiltInRegistries.ITEM, itemKey,
			new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void initialize() {
		for (int i = 0; i < UNWAXED.size(); i++) {
			if (i + 1 < UNWAXED.size()) {
				OxidizableBlocksRegistry.registerNextStage(UNWAXED.get(i), UNWAXED.get(i + 1));
			}
			OxidizableBlocksRegistry.registerWaxable(UNWAXED.get(i), WAXED.get(i));
		}
		ResourceKey<CreativeModeTab> functional = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
			Identifier.withDefaultNamespace("functional_blocks"));
		CreativeModeTabEvents.modifyOutputEvent(functional).register(output -> output.insertAfter(Items.FURNACE,
			COPPER_FURNACE, EXPOSED_COPPER_FURNACE, WEATHERED_COPPER_FURNACE, OXIDIZED_COPPER_FURNACE,
			WAXED_COPPER_FURNACE, WAXED_EXPOSED_COPPER_FURNACE, WAXED_WEATHERED_COPPER_FURNACE, WAXED_OXIDIZED_COPPER_FURNACE));
	}
}
