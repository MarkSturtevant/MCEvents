package com.marks.mcevents.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBingo extends Game {
	
	private static final int NUM_ENCHANTED = 3;
	private List<BingoPlayer> bplist;
	
	public ItemBingo() {
		List<Player> players = GameManager.getActivePlayers();
		if (players.size() < 1 || players.size() > 7) {
			GameManager.broadcast("Either too little or too many players!");
			GameManager.stopGame();
		}
		
		ItemStack[] map = setItems();
		bplist = new ArrayList<>();
		for (Player p : players)
			bplist.add(new BingoPlayer(p, map, this));
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean removePlayer(Player p) {
		BingoPlayer out = null;
		for (BingoPlayer bp : bplist)
			if (bp.p == p) {
				out = bp;
				break;
			}
		
		if (out == null)
			return false;
		
		GameManager.broadcast(p.getName() + " has opted out of this ItemBingo game!");
		bplist.remove(out);
		return true;
	}
	
	public void sendEnchantFill(int id) {
		for (BingoPlayer bp : bplist)
			bp.flipID(id);
	}
	
	public void onRightClickEvent(Event event) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		BingoPlayer clicker = this.getPlayer(e.getPlayer());
		if (clicker == null)
			return;
		openBingoInventory(clicker.p, clicker);
		e.setCancelled(true);
	}
	
	public void onInventoryClickEvent(Event event) {
		InventoryClickEvent e = (InventoryClickEvent) event;
		BingoPlayer clicker = this.getPlayer((Player) e.getWhoClicked());
		if (clicker == null && ! e.getClickedInventory().getTitle().equals("Bingo Board"))
			return;
		e.setCancelled(true);
		ItemStack is = e.getCurrentItem();
		if (is != null && is.getType() == Material.SKULL_ITEM && is.getDurability() == 3)
			openBingoInventory((Player) e.getWhoClicked(), getPlayer(((SkullMeta) is.getItemMeta()).getOwner()));
	}
	
	private void openBingoInventory(Player p, BingoPlayer view) {
		p.closeInventory();
		List<String> names = getAllPlayerNames();
		Inventory inv = Bukkit.createInventory(p, 9 * 7, "Bingo Board");
		for (int i = 1; i < 9 * 7; i += 9)
			inv.setItem(i, new ItemStack(Material.DRAGON_EGG));
		for (int i = 0; i < 25; ++i)
			inv.setItem((i / 5 + 1) * 9 + i % 4, view.items[i]);
		for (int i = 0; i < names.size(); ++i) {
			ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta sm = (SkullMeta) is.getItemMeta();
			sm.setOwner(names.get(i));
			if (view.p.getName().equals(names.get(i))) {
				sm.addEnchant(Enchantment.DURABILITY, 1, true);
				sm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			is.setItemMeta(sm);
			inv.setItem(i * 9, is);
		}
	}
	
	public List<String> getAllPlayerNames() {
		List<String> strList = new ArrayList<>();
		bplist.forEach(bp -> strList.add(bp.p.getName()));
		return strList;
	}
	
	public BingoPlayer getPlayer(Player p) {
		for (BingoPlayer bp : bplist)
			if (bp.p == p)
				return bp;
		return null;
	}
	
	public BingoPlayer getPlayer(String pName) {
		for (BingoPlayer bp : bplist)
			if (bp.p.getName().equals(pName))
				return bp;
		return null;
	}
	
	private ItemStack[] setItems() {
		ItemStack[] map = new ItemStack[25];
		// creating grab lists.
		List<ItemStack> earlyGame = Arrays.asList(new ItemStack(Material.STONE), new ItemStack(Material.DIRT), new ItemStack(Material.COBBLESTONE), 
				new ItemStack(Material.WOOD), new ItemStack(Material.SAPLING), new ItemStack(Material.SAND), new ItemStack(Material.GRAVEL), new ItemStack(Material.IRON_ORE), 
				new ItemStack(Material.LOG), new ItemStack(Material.GLASS), new ItemStack(Material.SANDSTONE), new ItemStack(Material.WOOL), new ItemStack(Material.YELLOW_FLOWER), 
				new ItemStack(Material.RED_ROSE), new ItemStack(Material.BROWN_MUSHROOM), new ItemStack(Material.RED_MUSHROOM), new ItemStack(Material.BRICK), new ItemStack(Material.TORCH), 
				new ItemStack(Material.WOOD_STAIRS), new ItemStack(Material.CHEST), new ItemStack(Material.WORKBENCH), new ItemStack(Material.FURNACE), new ItemStack(Material.WOODEN_DOOR), 
				new ItemStack(Material.LADDER), new ItemStack(Material.COBBLESTONE_STAIRS), new ItemStack(Material.LEVER), new ItemStack(Material.STONE_PLATE), new ItemStack(Material.WOOD_PLATE), 
				new ItemStack(Material.CLAY), new ItemStack(Material.FENCE), new ItemStack(Material.STAINED_GLASS), new ItemStack(Material.TRAP_DOOR), new ItemStack(Material.THIN_GLASS), 
				new ItemStack(Material.FENCE_GATE), new ItemStack(Material.SANDSTONE_STAIRS), new ItemStack(Material.SPRUCE_WOOD_STAIRS), new ItemStack(Material.BIRCH_WOOD_STAIRS), 
				new ItemStack(Material.JUNGLE_WOOD_STAIRS), new ItemStack(Material.STAINED_CLAY), new ItemStack(Material.LOG_2), 
				new ItemStack(Material.ACACIA_STAIRS), new ItemStack(Material.DARK_OAK_STAIRS), new ItemStack(Material.CARPET), new ItemStack(Material.HARD_CLAY), new ItemStack(Material.CONCRETE), 
				new ItemStack(Material.CONCRETE_POWDER), new ItemStack(Material.WOOD_SWORD), new ItemStack(Material.WOOD_SPADE), new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE), 
				new ItemStack(Material.STONE_SWORD), new ItemStack(Material.STONE_SPADE), new ItemStack(Material.STONE_PICKAXE), new ItemStack(Material.STONE_AXE), new ItemStack(Material.STICK), 
				new ItemStack(Material.BOWL), new ItemStack(Material.WOOD_HOE), new ItemStack(Material.STONE_HOE), new ItemStack(Material.SEEDS), new ItemStack(Material.FLINT), 
				new ItemStack(Material.PORK), new ItemStack(Material.GRILLED_PORK), new ItemStack(Material.PAINTING), new ItemStack(Material.SIGN), new ItemStack(Material.WOOD_DOOR), 
				new ItemStack(Material.BOAT), new ItemStack(Material.LEATHER), new ItemStack(Material.CLAY_BRICK), new ItemStack(Material.CLAY_BALL), new ItemStack(Material.SUGAR_CANE), 
				new ItemStack(Material.PAPER), new ItemStack(Material.SUGAR), new ItemStack(Material.BED), new ItemStack(Material.RAW_BEEF), new ItemStack(Material.COOKED_BEEF), 
				new ItemStack(Material.RAW_CHICKEN), new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.GLASS_BOTTLE), new ItemStack(Material.ITEM_FRAME), 
				new ItemStack(Material.FLOWER_POT_ITEM), new ItemStack(Material.ARMOR_STAND), new ItemStack(Material.MUTTON), new ItemStack(Material.COOKED_MUTTON), 
				new ItemStack(Material.BANNER), new ItemStack(Material.SPRUCE_DOOR_ITEM), new ItemStack(Material.BIRCH_DOOR_ITEM), new ItemStack(Material.JUNGLE_DOOR_ITEM), 
				new ItemStack(Material.ACACIA_DOOR_ITEM), new ItemStack(Material.DARK_OAK_DOOR_ITEM), new ItemStack(Material.BOAT_SPRUCE), new ItemStack(Material.BOAT_BIRCH), 
				new ItemStack(Material.BOAT_JUNGLE), new ItemStack(Material.BOAT_ACACIA), new ItemStack(Material.BOAT_DARK_OAK), new ItemStack(Material.IRON_NUGGET),
				new ItemStack(Material.COAL, 1, (short) 1));

		List<ItemStack> midGame = Arrays.asList(new ItemStack(Material.GOLD_ORE), new ItemStack(Material.LEAVES), new ItemStack(Material.DISPENSER), new ItemStack(Material.NOTE_BLOCK), 
				new ItemStack(Material.POWERED_RAIL), new ItemStack(Material.DETECTOR_RAIL), new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.OBSIDIAN), 
				new ItemStack(Material.RAILS), new ItemStack(Material.JUKEBOX), new ItemStack(Material.NETHERRACK), new ItemStack(Material.IRON_FENCE), new ItemStack(Material.BRICK_STAIRS), 
				new ItemStack(Material.TRIPWIRE_HOOK), new ItemStack(Material.TRAPPED_CHEST), new ItemStack(Material.GOLD_PLATE), new ItemStack(Material.IRON_PLATE), 
				new ItemStack(Material.REDSTONE_BLOCK), new ItemStack(Material.HOPPER), new ItemStack(Material.ACTIVATOR_RAIL), new ItemStack(Material.DROPPER), new ItemStack(Material.IRON_TRAPDOOR), 
				new ItemStack(Material.COAL_BLOCK), new ItemStack(Material.IRON_SPADE), new ItemStack(Material.IRON_PICKAXE), new ItemStack(Material.IRON_AXE), new ItemStack(Material.FLINT_AND_STEEL), 
				new ItemStack(Material.COAL), new ItemStack(Material.DIAMOND), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.IRON_SWORD), 
				new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.DIAMOND_SPADE), new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.DIAMOND_AXE), 
				new ItemStack(Material.GOLD_SWORD), new ItemStack(Material.GOLD_SPADE), new ItemStack(Material.GOLD_PICKAXE), new ItemStack(Material.GOLD_AXE), new ItemStack(Material.IRON_HOE), 
				new ItemStack(Material.DIAMOND_HOE), new ItemStack(Material.GOLD_HOE), new ItemStack(Material.WHEAT), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), 
				new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_BOOTS), 
				new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.BUCKET), 
				new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.MINECART), new ItemStack(Material.IRON_DOOR), new ItemStack(Material.REDSTONE), 
				new ItemStack(Material.STORAGE_MINECART), new ItemStack(Material.POWERED_MINECART), new ItemStack(Material.COMPASS), new ItemStack(Material.WATCH), new ItemStack(Material.MAP), 
				new ItemStack(Material.SHEARS), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.CAULDRON_ITEM), new ItemStack(Material.HOPPER_MINECART), new ItemStack(Material.SHIELD));

		List<ItemStack> lateGame = Arrays.asList(new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.SOUL_SAND), 
				new ItemStack(Material.GLOWSTONE), new ItemStack(Material.NETHER_BRICK), new ItemStack(Material.NETHER_FENCE), new ItemStack(Material.NETHER_BRICK_STAIRS), 
				new ItemStack(Material.NETHER_WARTS), new ItemStack(Material.ENCHANTMENT_TABLE), new ItemStack(Material.ENDER_CHEST), new ItemStack(Material.EMERALD_BLOCK), 
				new ItemStack(Material.ANVIL), new ItemStack(Material.DAYLIGHT_DETECTOR), new ItemStack(Material.QUARTZ_BLOCK), new ItemStack(Material.QUARTZ_STAIRS), 
				new ItemStack(Material.SLIME_BLOCK), new ItemStack(Material.MAGMA), new ItemStack(Material.NETHER_WART_BLOCK), new ItemStack(Material.RED_NETHER_BRICK), 
				new ItemStack(Material.OBSERVER), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.GLOWSTONE_DUST), 
				new ItemStack(Material.BLAZE_ROD), new ItemStack(Material.GHAST_TEAR), new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.MAGMA_CREAM), 
				new ItemStack(Material.BREWING_STAND_ITEM), new ItemStack(Material.EYE_OF_ENDER), new ItemStack(Material.FIREBALL), new ItemStack(Material.REDSTONE_COMPARATOR), 
				new ItemStack(Material.NETHER_BRICK_ITEM), new ItemStack(Material.QUARTZ));

		List<ItemStack> explor = Arrays.asList(new ItemStack(Material.TNT), new ItemStack(Material.BOOKSHELF), new ItemStack(Material.MOSSY_COBBLESTONE), new ItemStack(Material.SNOW_BLOCK), 
				new ItemStack(Material.CACTUS), new ItemStack(Material.PUMPKIN), new ItemStack(Material.JACK_O_LANTERN), new ItemStack(Material.VINE), new ItemStack(Material.WATER_LILY), 
				new ItemStack(Material.COCOA), new ItemStack(Material.STAINED_GLASS_PANE), new ItemStack(Material.LEAVES_2), new ItemStack(Material.PRISMARINE), new ItemStack(Material.SEA_LANTERN), 
				new ItemStack(Material.HAY_BLOCK), new ItemStack(Material.RED_SANDSTONE), new ItemStack(Material.RED_SANDSTONE_STAIRS), new ItemStack(Material.BONE_BLOCK), 
				new ItemStack(Material.LIME_GLAZED_TERRACOTTA), new ItemStack(Material.PINK_GLAZED_TERRACOTTA), new ItemStack(Material.GRAY_GLAZED_TERRACOTTA), 
				new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA), new ItemStack(Material.BROWN_GLAZED_TERRACOTTA), new ItemStack(Material.BLACK_GLAZED_TERRACOTTA), 
				new ItemStack(Material.APPLE), new ItemStack(Material.BOW), new ItemStack(Material.ARROW), new ItemStack(Material.MUSHROOM_SOUP), new ItemStack(Material.STRING), 
				new ItemStack(Material.FEATHER), new ItemStack(Material.SULPHUR), new ItemStack(Material.BREAD), new ItemStack(Material.LEATHER_HELMET),  new ItemStack(Material.IRON_BARDING),
				new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.CHAINMAIL_HELMET), 
				new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.GOLDEN_APPLE), 
				new ItemStack(Material.SADDLE), new ItemStack(Material.SNOW_BALL), new ItemStack(Material.MILK_BUCKET), new ItemStack(Material.BOOK), new ItemStack(Material.SLIME_BALL), 
				new ItemStack(Material.EGG), new ItemStack(Material.FISHING_ROD), new ItemStack(Material.RAW_FISH), new ItemStack(Material.COOKED_FISH), new ItemStack(Material.INK_SACK), 
				new ItemStack(Material.BONE), new ItemStack(Material.CAKE), new ItemStack(Material.COOKIE), new ItemStack(Material.MELON), new ItemStack(Material.PUMPKIN_SEEDS), 
				new ItemStack(Material.MELON_SEEDS), new ItemStack(Material.ROTTEN_FLESH), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.POTION), new ItemStack(Material.SPIDER_EYE), 
				new ItemStack(Material.FERMENTED_SPIDER_EYE), new ItemStack(Material.SPECKLED_MELON), new ItemStack(Material.EXP_BOTTLE), new ItemStack(Material.BOOK_AND_QUILL), 
				new ItemStack(Material.WRITTEN_BOOK), new ItemStack(Material.EMERALD), new ItemStack(Material.CARROT_ITEM), new ItemStack(Material.POTATO_ITEM), new ItemStack(Material.BAKED_POTATO), 
				new ItemStack(Material.POISONOUS_POTATO), new ItemStack(Material.GOLDEN_CARROT), new ItemStack(Material.CARROT_STICK), new ItemStack(Material.PUMPKIN_PIE), 
				new ItemStack(Material.FIREWORK), new ItemStack(Material.FIREWORK_CHARGE), new ItemStack(Material.ENCHANTED_BOOK), new ItemStack(Material.EXPLOSIVE_MINECART), 
				new ItemStack(Material.PRISMARINE_SHARD), new ItemStack(Material.PRISMARINE_CRYSTALS), new ItemStack(Material.RABBIT), new ItemStack(Material.COOKED_RABBIT), 
				new ItemStack(Material.RABBIT_STEW), new ItemStack(Material.RABBIT_FOOT), new ItemStack(Material.RABBIT_HIDE), new ItemStack(Material.GOLD_BARDING), 
				new ItemStack(Material.DIAMOND_BARDING), new ItemStack(Material.LEASH), new ItemStack(Material.NAME_TAG), new ItemStack(Material.BEETROOT), new ItemStack(Material.BEETROOT_SEEDS), 
				new ItemStack(Material.BEETROOT_SOUP), new ItemStack(Material.TOTEM), new ItemStack(Material.GOLD_RECORD), new ItemStack(Material.GREEN_RECORD),
				new ItemStack(Material.INK_SACK, 1, (short) 3), new ItemStack(Material.RAW_FISH, 1, (short) 1), new ItemStack(Material.RAW_FISH, 1, (short) 2), 
				new ItemStack(Material.RAW_FISH, 1, (short) 3), new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), new ItemStack(Material.RED_ROSE, 1, (short) 2),
				new ItemStack(Material.RED_ROSE, 1, (short) 1), new ItemStack(Material.RED_ROSE, 1, (short) 3), new ItemStack(Material.RED_ROSE, 1, (short) 8), 
				new ItemStack(Material.RED_ROSE, 1, (short) 5));

		
		// filling item list
		Random rand = new Random();
		List<ItemStack> toAdd = new ArrayList<>();
		while (toAdd.size() < 5) {ItemStack next = earlyGame.get(rand.nextInt(earlyGame.size())); if (! toAdd.contains(next)) toAdd.add(next);}
		while (toAdd.size() < 10) {ItemStack next = midGame.get(rand.nextInt(midGame.size())); if (! toAdd.contains(next)) toAdd.add(next);}
		while (toAdd.size() < 15) {ItemStack next = lateGame.get(rand.nextInt(lateGame.size())); if (! toAdd.contains(next)) toAdd.add(next);}
		while (toAdd.size() < 25) {ItemStack next = explor.get(rand.nextInt(explor.size())); if (! toAdd.contains(next)) toAdd.add(next);}
		
		// filling map
		int[] dist = new int[] {0, 3, 1, 4, 2};
		int randStart = rand.nextInt(5);
		for (int i = 0; i < dist.length; ++i) dist[i] += randStart;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; ++j)
				map[(dist[i] + i) % 5 + j * 5] = toAdd.get(j * 5 + i);
		
		// adding enchants
		List<Integer> enchanted = new ArrayList<>();
		while (enchanted.size() < NUM_ENCHANTED) {
			int randInt = rand.nextInt(25);
			if (! enchanted.contains(randInt)) {
				ItemMeta im = map[randInt].getItemMeta();
				im.addEnchant(Enchantment.DURABILITY, 1, true);
				im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				map[randInt].setItemMeta(im);
				enchanted.add(randInt);
			}
		}
		
		return map;
	}

}

class BingoPlayer {
	
	ItemStack[] specials;
	ItemStack[] items;
	Player p;
	ItemBingo gameRef;
	
	public BingoPlayer(Player p, ItemStack[] itemstacks, ItemBingo game) {
		this.p = p;
		this.gameRef = game;
		this.setItemStacks(itemstacks);
		this.p.getInventory().clear();
		this.p.getInventory().setItem(8, specials[2]);
	}
	
	private void setItemStacks(ItemStack[] itemstacks) {
		specials = new ItemStack[] {new ItemStack(Material.BARRIER), new ItemStack(Material.SKULL_ITEM, 1, (short) 3), new ItemStack(Material.PAINTING)};
		SkullMeta sm = (SkullMeta) specials[1].getItemMeta();
		sm.setOwner(p.getName());
		specials[1].setItemMeta(sm);
		ItemMeta im = specials[2].getItemMeta();
		im.setDisplayName(ChatColor.GRAY + "Bingo Board");
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		specials[2].setItemMeta(im);
		
		items = new ItemStack[25];
		for (int i = 0; i < 25; i++)
			items[i] = itemstacks[i];
	}
	
	public void testInventory() {
		PlayerInventory pi = p.getInventory();
		boolean updated = false;
		for (int i = 0; i < 25; ++i)
			if (items[i] != specials[0] && items[i] != specials[1]) {
				ItemStack checker = new ItemStack(items[i].getType(), 1, items[i].getDurability());
				if (pi.containsAtLeast(checker, 1)) {
					this.secure(i);
					updated = true;
				}
			}
		if (updated)
			checkBingo();
	}
	
	private void secure(int id) {
		if (items[id].getItemMeta().getEnchants().keySet().size() > 0) {
			gameRef.sendEnchantFill(id);
			GameManager.broadcast(ChatColor.RED + p.getName() + " has found an enchanted item!");
		}
		else GameManager.broadcast(ChatColor.GREEN + p.getName() + " has found an item!");
	}
	
	private void checkBingo() {
		boolean[] got = new boolean[25];
		for (int i = 0; i < 25; ++i)
			got[i] = items[i] == specials[1];
		if ((got[0] && got[5] && got[10] && got[15] && got[20]) || 
				(got[1] && got[6] && got[11] && got[16] && got[21]) || 
				(got[2] && got[7] && got[12] && got[17] && got[22]) || 
				(got[3] && got[8] && got[13] && got[18] && got[23]) || 
				(got[4] && got[9] && got[14] && got[19] && got[24]) || 
				(got[0] && got[1] && got[2] && got[3] && got[4]) || 
				(got[5] && got[6] && got[7] && got[8] && got[9]) || 
				(got[10] && got[11] && got[12] && got[13] && got[14]) || 
				(got[15] && got[16] && got[17] && got[18] && got[19]) || 
				(got[20] && got[21] && got[22] && got[23] && got[24]) || 
				(got[0] && got[6] && got[12] && got[18] && got[24]) || 
				(got[4] && got[8] && got[12] && got[16] && got[20]))
			GameManager.broadcast(ChatColor.BOLD + p.getName() + " has made a " + ChatColor.GOLD + "BINGO" + ChatColor.WHITE + "!");
	}
	
	public void flipID(int id) {
		if (items[id] != specials[1])
			items[id] = specials[0];
	}
	
}

// early game
// mid game
// late game
// morely lucky find
// morely lucky find