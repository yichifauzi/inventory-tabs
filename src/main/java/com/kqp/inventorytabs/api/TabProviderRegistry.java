package com.kqp.inventorytabs.api;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.init.InventoryTabsClient;
import com.kqp.inventorytabs.tabs.provider.*;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Registry for tab providers.
 */
public class TabProviderRegistry {
	private static final Map<Identifier, TabProvider> TAB_PROVIDERS = new HashMap<>();

	public static final PlayerInventoryTabProvider PLAYER_INVENTORY_TAB_PROVIDER = (PlayerInventoryTabProvider) register(
			InventoryTabs.id("player_inventory_tab_provider"), new PlayerInventoryTabProvider());
	public static final SimpleBlockTabProvider SIMPLE_BLOCK_TAB_PROVIDER = (SimpleBlockTabProvider) register(
			InventoryTabs.id("simple_block_tab_provider"), new SimpleBlockTabProvider());
	public static final ChestTabProvider CHEST_TAB_PROVIDER = (ChestTabProvider) register(
			InventoryTabs.id("chest_tab_provider"), new ChestTabProvider());
	public static final EnderChestTabProvider ENDER_CHEST_TAB_PROVIDER = (EnderChestTabProvider) register(
			InventoryTabs.id("ender_chest_tab_provider"), new EnderChestTabProvider());
	public static final ShulkerBoxTabProvider SHULKER_BOX_TAB_PROVIDER = (ShulkerBoxTabProvider) register(
			InventoryTabs.id("shulker_box_tab_provider"), new ShulkerBoxTabProvider());
	public static final LecternTabProvider LECTERN_TAB_PROVIDER = (LecternTabProvider) register(
			InventoryTabs.id("lectern_tab_provider"), new LecternTabProvider());
	public static final InventoryTabProvider INVENTORY_TAB_PROVIDER = (InventoryTabProvider) register(
			InventoryTabs.id("inventory_tab_provider"), new InventoryTabProvider());

	public static final List<Identifier> SIMPLE_BLOCK_ENTITIES = Stream.of(
			"minecraft:dispenser",
			"minecraft:furnace",
			"minecraft:enchanting_table",
			"minecraft:brewing_stand",
			"minecraft:beacon",
			"minecraft:hopper",
			"minecraft:dropper",
			"minecraft:shulker_box",
			"minecraft:white_shulker_box",
			"minecraft:orange_shulker_box",
			"minecraft:magenta_shulker_box",
			"minecraft:light_blue_shulker_box",
			"minecraft:yellow_shulker_box",
			"minecraft:lime_shulker_box",
			"minecraft:pink_shulker_box",
			"minecraft:gray_shulker_box",
			"minecraft:light_gray_shulker_box",
			"minecraft:cyan_shulker_box",
			"minecraft:purple_shulker_box",
			"minecraft:blue_shulker_box",
			"minecraft:brown_shulker_box",
			"minecraft:green_shulker_box",
			"minecraft:red_shulker_box",
			"minecraft:black_shulker_box",
			"minecraft:barrel",
			"minecraft:smoker",
			"minecraft:blast_furnace",
			"minecraft:lectern",
			"aurorasdeco:shelf/oak",
			"aurorasdeco:shelf/spruce",
			"aurorasdeco:shelf/birch",
			"aurorasdeco:shelf/jungle",
			"aurorasdeco:shelf/acacia",
			"aurorasdeco:shelf/dark_oak",
			"aurorasdeco:shelf/mangrove",
			"aurorasdeco:shelf/azalea",
			"aurorasdeco:shelf/crimson",
			"aurorasdeco:shelf/warped",
			"aurorasdeco:shelf/jacaranda",
			"aurorasdeco:shelf/botania/livingwood",
			"aurorasdeco:shelf/botania/mossy_livingwood",
			"aurorasdeco:shelf/botania/dreamwood",
			"aurorasdeco:shelf/botania/mossy_dreamwood",
			"aurorasdeco:shelf/yttr/soul",
			"yttr:levitation_chamber",
			"yttr:centrifuge",
			"yttr:dopper",
			"yttr:flopper",
			"yttr:suit_station",
			"yttr:can_filler",
			"yttr:project_table",
			"yttr:dyed_project_table",
			"yttr:ssd",
			"farmersdelight:cooking_pot",
			"farmersdelight:basket",
			"farmersdelight:oak_cabinet",
			"farmersdelight:birch_cabinet",
			"farmersdelight:spruce_cabinet",
			"farmersdelight:jungle_cabinet",
			"farmersdelight:acacia_cabinet",
			"farmersdelight:dark_oak_cabinet",
			"farmersdelight:mangrove_cabinet",
			"farmersdelight:crimson_cabinet",
			"farmersdelight:warped_cabinet",
			"create:schematicannon",
			"create:schematic_table",
			"create:stockpile_switch",
			"create:display_link",
			"create:white_toolbox",
			"create:orange_toolbox",
			"create:magenta_toolbox",
			"create:light_blue_toolbox",
			"create:yellow_toolbox",
			"create:lime_toolbox",
			"create:pink_toolbox",
			"create:gray_toolbox",
			"create:light_gray_toolbox",
			"create:cyan_toolbox",
			"create:purple_toolbox",
			"create:blue_toolbox",
			"create:brown_toolbox",
			"create:green_toolbox",
			"create:red_toolbox",
			"create:black_toolbox"
			).map(Identifier::tryParse).toList();


	public static void init() {
		Registry.BLOCK.forEach(block -> {
			if (block instanceof BlockEntityProvider) {
				if (block instanceof AbstractChestBlock) {
					InventoryTabsClient.LOGGER.info("Registering chest tab {}", block);
					registerChest(block);
				} else if (SIMPLE_BLOCK_ENTITIES.contains(Registry.BLOCK.getId(block))) {
					InventoryTabsClient.LOGGER.info("Registering simple block tab {}", block);
					registerSimpleBlock(block);
				}
			} else if ((block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock)) || block instanceof AnvilBlock || block instanceof CartographyTableBlock || block instanceof GrindstoneBlock || block instanceof LoomBlock || block instanceof StonecutterBlock) {
				registerSimpleBlock(block);
			}
		});
	}

	public static void registerInventoryTab(Identifier itemId) {
		INVENTORY_TAB_PROVIDER.addItem(itemId);
	}

	/**
	 * Used to register a block with the simple block tab provider.
	 *
	 * @param block
	 */
	public static void registerSimpleBlock(Block block) {
		SIMPLE_BLOCK_TAB_PROVIDER.addBlock(block);
	}

	/**
	 * Used to register a block identifier with the simple block tab provider.
	 *
	 * @param blockId
	 */
	public static void registerSimpleBlock(Identifier blockId) {
		SIMPLE_BLOCK_TAB_PROVIDER.addBlock(blockId);
	}

	public static void removeSimpleBlock(Block block) {
		SIMPLE_BLOCK_TAB_PROVIDER.removeBlock(block);
	}

	public static void removeSimpleBlock(Identifier blockId) {
		SIMPLE_BLOCK_TAB_PROVIDER.removeBlock(blockId);
	}

	/**
	 * Used to register a chest with the chest tab provider.
	 *
	 * @param block
	 */
	public static void registerChest(Block block) {
		CHEST_TAB_PROVIDER.addChestBlock(block);
	}

	/**
	 * Used to register a chest with the chest tab provider.
	 *
	 * @param blockId
	 */
	public static void registerChest(Identifier blockId) {
		CHEST_TAB_PROVIDER.addChestBlock(blockId);
	}

	public static TabProvider register(Identifier id, TabProvider tabProvider) {
		TAB_PROVIDERS.put(id, tabProvider);

		return tabProvider;
	}

	public static List<TabProvider> getTabProviders() {
		return new ArrayList<>(TAB_PROVIDERS.values());
	}
}
