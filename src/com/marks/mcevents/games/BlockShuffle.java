package com.marks.mcevents.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.mcevents.Main;

public class BlockShuffle extends Game {
	
	private List<Combo> combos;
	private int gameTaskId;
	private int timer, endTime, difficulty, catcher;
	
	public BlockShuffle() {
		List<Player> players = GameManager.getActivePlayers();
		combos = new ArrayList<>();
		players.forEach(p -> combos.add(new Combo(p)));
		
		if (players.size() < 1) {
			GameManager.broadcast("Blockswap needs at least one player!");
			GameManager.stopGame();
		}
	}

	@Override
	public void start() {
		timer = endTime = difficulty = catcher = 0;
		for (Combo c : combos)
			c.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0, true, false));
		setNewBlocks();
		gameTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			++timer;
			for (Combo c : combos)
				c.testBlock(this);
			if (catcher == 0 && endTime - timer < 60 * 20) {
				++catcher;
				GameManager.broadcast(ChatColor.DARK_RED + "1 minute left to find your blocks!");
			}
			if (catcher == 1 && endTime - timer < 10 * 20) {
				++catcher;
				GameManager.broadcast(ChatColor.DARK_RED + "" + ChatColor.BOLD + "10 seconds left to find your blocks!");
			}
			if (catcher == 2 && endTime < timer) {
				catcher = 0;
				this.eliminate();
			}
		}, 0L, 1L);
	}

	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(gameTaskId);
	}
	
	private void setNewBlocks() {
		catcher = 0;
		for (Combo c : combos)
			c.setNextBlock(difficulty);
		difficulty++;
		if (timer < 40 * 60 * 20)
			endTime = timer + 5 * 60 * 20;
		else if (timer < 60 * 60 * 20)
			endTime = timer + (12 - timer / 60 / 20 / 5) * 60 * 20;
		else endTime = timer + 60 * 20;
	}
	
	private void eliminate() {
		for (int i = 0; i < combos.size(); ++i) {
			if (! combos.get(i).hasFound()) {
				Player p = combos.get(i).getPlayer();
				GameManager.broadcast(ChatColor.DARK_BLUE + p.getName() + " has been eliminated!");
				p.getWorld().createExplosion(p.getLocation(), 10.0f);
				combos.remove(i);
				i--;
			}
		}
		if (combos.size() <= 1)
			declareWinner();
		else this.setNewBlocks();
	}

	@Override
	public boolean removePlayer(Player p) {
		boolean found = false;
		for (int i = 0; i < combos.size(); ++i)
			if (combos.get(i).getPlayer() == p) {
				combos.remove(i);
				found = true;
				break;
			}
		if (combos.size() == 1)
			declareWinner();
		return found;
	}
	
	private void declareWinner() {
		if (combos.size() == 0)
			GameManager.broadcast(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "This blockswap game is a draw!");
		else GameManager.broadcast(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + combos.get(0).getPlayer().getName() + " has won blockswap!");
		GameManager.stopGame();
	}
	
	protected void onFind(Combo c) {
		GameManager.broadcast(ChatColor.GREEN + "" + ChatColor.BOLD + c.getPlayer().getName() + " has found their block!");
		for (Combo co : combos)
			if (! co.hasFound())
				return;
		setNewBlocks();
	}

}

class Combo {
	private Player p;
	private Material mat;
	private int data;
	private boolean found;
	
	public Combo(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public boolean hasFound() {
		return found;
	}
	
	@SuppressWarnings("deprecation")
	public void testBlock(BlockShuffle bs) {
		if (found)
			return;
		for (int i = 0; i >= -1; --i) {
			Location l = p.getLocation().add(0, i, 0);
			boolean matching = false;
			if (l.getBlock().getType().equals(mat)) {
				if (data == -1)
					matching = true;
				else matching = l.getBlock().getData() == data;
			}
			if (matching) {
				found = true;
				bs.onFind(this);
			}
		}
	}
	
	public void setNextBlock(int difficulty) {
		found = false;
		Random random = new Random();
		double[] probs = getProbs(difficulty);
		double rand = random.nextDouble();
		if (rand < probs[0]) {
			Easy block = Easy.values()[random.nextInt(Easy.values().length)];
			this.mat = block.mat; this.data = block.data;
			this.p.sendMessage(ChatColor.ITALIC + "" + ChatColor.YELLOW + "Your next block is " + ChatColor.RED + block.title + ChatColor.YELLOW + ".");
		}
		else if (rand < probs[0] + probs[1]) {
			Med block = Med.values()[random.nextInt(Med.values().length)];
			this.mat = block.mat; this.data = block.data;
			this.p.sendMessage(ChatColor.ITALIC + "" + ChatColor.YELLOW + "Your next block is " + ChatColor.RED + block.title + ChatColor.YELLOW + ".");
		}
		else {
			Hard block = Hard.values()[random.nextInt(Hard.values().length)];
			this.mat = block.mat; this.data = block.data;
			this.p.sendMessage(ChatColor.ITALIC + "" + ChatColor.YELLOW + "Your next block is " + ChatColor.RED + block.title + ChatColor.YELLOW + ".");
		}
	}
	
	private double[] getProbs(int difficulty) {
		switch(difficulty) {
		case 0:
			return new double[] {1.0, 0, 0};
		case 1:
			return new double[] {0.9, 0.1, 0};
		case 2:
			return new double[] {0.5, 0.5, 0};
		case 3:
			return new double[] {0.25, 0.75, 0};
		case 4:
			return new double[] {0.1, 0.9, 0};
		case 5:
			return new double[] {0, 1.0, 0};
		case 6:
			return new double[] {0, 0.85, 0.15};
		case 7:
			return new double[] {0, 0.5, 0.5};
		case 8:
			return new double[] {0, 0.25, 0.75};
		default:
			return new double[] {0.05, 0.45, 0.5};
		}
		
	}
	
	public enum Easy {
		CACTUS(Material.CACTUS, -1, "Cactus"),
		BEDROCK(Material.BEDROCK, -1, "Bedrock"),
		BRICK(Material.BRICK, -1, "Bricks"),
		BRICK_STAIRS(Material.BRICK_STAIRS, -1, "Brick Stairs"),
		CHEST(Material.CHEST, -1, "Chest"),
		CLAY(Material.CLAY, -1, "Clay"),
		COAL_BLOCK(Material.COAL_BLOCK, -1, "Coal Block"),
		COAL_ORE(Material.COAL_ORE, -1, "Coal Ore"),
		COBBLESTONE(Material.COBBLESTONE, -1, "Cobblestone"),
		WORKBENCH(Material.WORKBENCH, -1, "Crafting Table"),
		DIRT(Material.DIRT, 1, "Coarse Dirt"),
		FURNACE(Material.FURNACE, -1, "Furnace"),
		GLASS(Material.GLASS, -1, "Glass"),
		GRASS_PATH(Material.GRASS_PATH, -1, "Grass Path"),
		GRAVEL(Material.GRAVEL, -1, "Gravel"),
		IRON_PLATE(Material.IRON_PLATE, -1, "Iron Pressure Plate"),
		LEAVES(Material.LEAVES, -1, "Oak/Birch/Spruce/Jungle Leaves"),
		LADDER(Material.LADDER, -1, "Ladder"),
		LEVER(Material.LEVER, -1, "Lever"),
		SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK, -1, "Sugar Cane"),
		SAND(Material.SAND, -1, "Sand"),
		SANDSTONE(Material.SANDSTONE, -1, "Sandstone"),
		SAPLING(Material.SAPLING, -1, "Sapling"),
		STONE(Material.STONE, 2, "Polished Granite"),
		STONEE(Material.STONE, 4, "Polished Diorite"),
		STONEEE(Material.STONE, 6, "Polished Andesite"),
		STONE_PLATE(Material.STONE_PLATE, -1, "Stone Pressure Plate"),
		WOOD_PLATE(Material.WOOD_PLATE, -1, "Wooden Pressure Plate"),
		TRIPWIRE(Material.TRIPWIRE, -1, "Tripwire (String)"),
		TRAP_DOOR(Material.TRAP_DOOR, -1, "Oak Trapdoor"),
		OAKLOG(Material.LOG, 0, "Oak/Birch/Spruce/Jungle Log"),
		WOOD(Material.WOOD, 0, "Oak Planks"),
		CONCRETEE(Material.CONCRETE, 4, "Yellow Concrete"),
		RED_GLAZED_TERRACOTTA(Material.RED_GLAZED_TERRACOTTA, -1, "Red Glazed Terracotta"),
		STAINED_CLAYE(Material.STAINED_CLAY, 1, "Orange Stained Clay"),
		RED_ROSEE(Material.RED_ROSE, -1, "Rose/Poppy"),
		YELLOW_FLOWER(Material.YELLOW_FLOWER, -1, "Dandelion");
		
		public Material mat;
		public int data;
		public String title;
		
		Easy(Material m, int data, String name) {
			this.mat = m;
			this.data = data;
			this.title = name;
		}
	}
	
	public enum Med {
		RAILS(Material.RAILS, -1, "Rail"),
		ACTIVATOR_RAIL(Material.ACTIVATOR_RAIL, -1, "Activator Rail"),
		BOOKSHELF(Material.BOOKSHELF, -1, "Bookshelf"),
		CAKE(Material.CAKE, -1, "Cake"),
		CAULDRON(Material.CAULDRON, -1, "Cauldron"),
		LOG_2(Material.LOG_2, -1, "Dark Oak/Acacia Log"),
		DAYLIGHT_DETECTOR(Material.DAYLIGHT_DETECTOR, -1, "Daylight Sensor"),
		DEAD_BUSH(Material.DEAD_BUSH, -1, "Dead Bush"),
		DETECTOR_RAIL(Material.DETECTOR_RAIL, -1, "Detector Rails"),
		DIAMOND_ORE(Material.DIAMOND_ORE, -1, "Diamond Ore"),
		BONE_BLOCK(Material.BONE_BLOCK, -1, "Bone Block"),
		DROPPER(Material.DROPPER, -1, "Dropper"),
		GLOWSTONE(Material.GLOWSTONE, -1, "Glowstone"),
		GOLD_ORE(Material.GOLD_ORE, -1, "Gold Ore"),
		POWERED_RAIL(Material.POWERED_RAIL, -1, "Powered Rail"),
		HAY_BLOCK(Material.HAY_BLOCK, -1, "Hay Bale"),
		HOPPER(Material.HOPPER, -1, "Hopper"),
		JUKEBOX(Material.JUKEBOX, -1, "Jukebox"),
		ICE(Material.ICE, -1, "Ice"),
		LAPIS_BLOCK(Material.LAPIS_BLOCK, -1, "Lapis Block"),
		LAPIS_ORE(Material.LAPIS_ORE, -1, "Lapis Ore"),
		LEAVES_2(Material.LEAVES_2, -1, "Dark Oak/Acacia Leaves"),
		GOLD_PLATE(Material.GOLD_PLATE, -1, "Golden Pressure Plate"),
		JACK_O_LANTERN(Material.JACK_O_LANTERN, -1, "Jack o'Lantern"),
		MAGMA(Material.MAGMA, -1, "Magma Block"),
		NETHER_BRICK(Material.NETHER_BRICK, -1, "Nether Brick"),
		NETHERRACK(Material.NETHERRACK, -1, "Netherrack"),
		NOTE_BLOCK(Material.NOTE_BLOCK, -1, "Note Block"),
		OBSIDIAN(Material.OBSIDIAN, -1, "Obsidian"),
		PISTON_BASE(Material.PISTON_BASE, -1, "Piston"),
		PUMPKIN(Material.PUMPKIN, -1, "Pumpkin"),
		QUARTZ_BLOCK(Material.QUARTZ_BLOCK, -1, "Quartz Block"),
		QUARTZ_ORE(Material.QUARTZ_ORE, -1, "Quartz Ore"),
		IRON_FENCE(Material.IRON_FENCE, -1, "Iron Bars"),
		IRON_BLOCK(Material.IRON_BLOCK, -1, "Iron Block"),
		IRON_ORE(Material.IRON_ORE, -1, "Iron Ore"),
		IRON_TRAPDOOR(Material.IRON_TRAPDOOR, -1, "Iron Trapdoor"),
		SNOW_BLOCK(Material.SNOW_BLOCK, -1, "Snow Block"),
		SOUL_SAND(Material.SOUL_SAND, -1, "Soul Sand"),
		REDSTONE_BLOCK(Material.REDSTONE_BLOCK, -1, "Redstone Block"),
		REDSTONE_ORE(Material.REDSTONE_ORE, -1, "Redstone Ore"),
		REDSTONE_LAMP_OFF(Material.REDSTONE_LAMP_OFF, -1, "Redstone Lamp"),
		TNT(Material.TNT, -1, "TNT"),
		TRAPPED_CHEST(Material.TRAPPED_CHEST, -1, "Trapped Chest"),
		TRIPWIRE_HOOK(Material.TRIPWIRE_HOOK, -1, "Tripwire Hook"),
		GREEN_GLAZED_TERRACOTTA(Material.GREEN_GLAZED_TERRACOTTA, -1, "Green Glazed Terracotta"),
		WOODEEE(Material.WOOD, 3, "Jungle Planks"),
		WOODEEEE(Material.WOOD, 4, "Acacia Planks"),
		WOODEEEEE(Material.WOOD, 5, "Dark Oak Planks"),
		WOODE(Material.WOOD, 1, "Spruce Planks"),
		WOODEE(Material.WOOD, 2, "Birch Planks"),
		CONCRETE_POWDER(Material.CONCRETE_POWDER, 5, "Lime Concrete Powder"),
		CONCRETE_POWDERE(Material.CONCRETE_POWDER, 6, "Pink Concrete Powder"),
		CONCRETE(Material.CONCRETE, 3, "Light Blue Concrete"),
		STAINED_GLASS_PANE(Material.STAINED_GLASS_PANE, 6, "Pink Stained Glass Pane"),
		STAINED_GLASS_PANEE(Material.STAINED_GLASS_PANE, 7, "Gray Stained Glass Pane"),
		STAINED_GLASS_PANEEE(Material.STAINED_GLASS_PANE, 8, "Light Gray Stained Glass Pane"),
		STAINED_GLASS(Material.STAINED_GLASS, 3, "Light Blue Stained Glass"),
		STAINED_GLASSE(Material.STAINED_GLASS, 4, "Yellow Stained Glass"),
		STAINED_GLASSEE(Material.STAINED_GLASS, 5, "Lime Stained Glass"),
		STAINED_CLAY(Material.STAINED_CLAY, 0, "White Stained Clay"),
		STAINED_CLAYEE(Material.STAINED_CLAY, 2, "Magenta Stained Clay"),
		WOOLE(Material.WOOL, 10, "Purple Wool"),
		WOOLEE(Material.WOOL, 11, "Blue Wool");
		
		public Material mat;
		public int data;
		public String title;
		
		Med(Material m, int data, String name) {
			this.mat = m;
			this.data = data;
			this.title = name;
		}
	}
	
	public enum Hard {
		ANVIL(Material.ANVIL, -1, "Anvil"),
		GOLD_BLOCK(Material.GOLD_BLOCK, -1, "Gold Block"),
		DIAMOND_BLOCK(Material.DIAMOND_BLOCK, -1, "Diamond Block"),
		DIRTE(Material.DIRT, 2, "Podzol"),
		DISPENSER(Material.DISPENSER, -1, "Dispenser"),
		EMERALD_BLOCK(Material.EMERALD_BLOCK, -1, "Emerald Block"),
		EMERALD_ORE(Material.EMERALD_ORE, -1, "Emerald Ore"),
		ENCHANTMENT_TABLE(Material.ENCHANTMENT_TABLE, -1, "Enchantment Table"),
		ENDER_PORTAL_FRAME(Material.ENDER_PORTAL_FRAME, -1, "End Portal Frame"),
		ENDER_CHEST(Material.ENDER_CHEST, -1, "Ender Chest"),
		MOB_SPAWNER(Material.MOB_SPAWNER, -1, "Mob Spawner"),
		MOSSY_COBBLESTONE(Material.MOSSY_COBBLESTONE, -1, "Mossy Cobblestone"),
		MYCEL(Material.MYCEL, -1, "Mycelium"),
		OBSERVER(Material.OBSERVER, -1, "Observer"),
		PACKED_ICE(Material.PACKED_ICE, -1, "Packed Ice"),
		RED_MUSHROOM(Material.RED_MUSHROOM, -1, "Red Mushroom"),
		RED_SANDSTONE(Material.RED_SANDSTONE, -1, "Red Sandstone"),
		RED_NETHER_BRICK(Material.RED_NETHER_BRICK, -1, "Red Nether Brick"),
		NETHER_WART_BLOCK(Material.NETHER_WART_BLOCK, -1, "Nether Wart Block"),
		PISTON_STICKY_BASE(Material.PISTON_STICKY_BASE, -1, "Sticky Piston"),
		//PRISMARINE(Material.PRISMARINE, -1, "Prismarine"),
		//SEA_LANTERN(Material.SEA_LANTERN, -1, "Sea Lantern"),
		SLIME_BLOCK(Material.SLIME_BLOCK, -1, "Slime Block"),
		//SPONGE(Material.SPONGE, -1, "Sponge"),
		BROWN_GLAZED_TERRACOTTA(Material.BROWN_GLAZED_TERRACOTTA, -1, "Brown Glazed Terracotta"),
		WOOL(Material.WOOL, 9, "Cyan Wool"),
		VINE(Material.VINE, -1, "Vines"),
		WEB(Material.WEB, -1, "Cobweb"),
		WATER_LILY(Material.WATER_LILY, -1, "Lily Pad"),
		RED_ROSE(Material.RED_ROSE, 2, "Allium"),
		BREWING_STAND(Material.BREWING_STAND, -1, "Brewing Stand"),
		BROWN_MUSHROOM(Material.BROWN_MUSHROOM, -1, "Brown Mushroom");
		
		public Material mat;
		public int data;
		public String title;
		
		Hard(Material m, int data, String name) {
			this.mat = m;
			this.data = data;
			this.title = name;
		}
	}
}