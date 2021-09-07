package com.marks.mcevents.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.marks.mcevents.Main;

public class Trapcraft extends Game {
	
	private Map<Player, Boolean> survivors;
	private List<Trap> activeTraps;
	private Player hunter, hunterTarget;
	private Location hunterSpawn, hunterLock;
	private int gameTaskId, compassTarget;
	private int[] cooldowns;
	
	private static final int SLOWTIME = 120, FREEZETIME = 12;
	
	public Trapcraft() {
		List<Player> players = GameManager.getActivePlayers();
		survivors = new HashMap<>();
		activeTraps = new ArrayList<>();
		if (players.size() < 2) {
			GameManager.broadcast("Trapcraft needs at least two players!");
			GameManager.stopGame();
			return;
		}
		hunter = players.remove(new Random().nextInt(players.size()));
		for (Player p : players)
			survivors.put(p, false);
		cooldowns = new int[3];
		compassTarget = 0;
	}

	@Override
	public void start() {
		hunter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SLOWTIME * 20, 2));
		hunterTarget = survivors.keySet().iterator().next();
		spreadPlayers();
		setHunterInventory();
		gameTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			for (int i = 0; i < cooldowns.length; ++i)
				if (cooldowns[i] > 0)
					cooldowns[i]--;
			if (cooldowns[1] > 0)
				hunter.teleport(hunterLock);
			if (cooldowns[2] == 0) {
				if (hunterTarget.getWorld().getEnvironment() == World.Environment.NORMAL)
					hunter.setCompassTarget(hunterTarget.getLocation());
				cooldowns[2] = 40;
			}
			for (int i = 0; i < activeTraps.size(); ++i) {
				if (activeTraps.get(i).trigger(hunter.getLocation())) {
					activeTraps.remove(i);
					i--;
				}
			}
		}, 0L, 1L);
	}

	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(gameTaskId);
	}
	
	private void spreadPlayers() {
		Iterator<Player> players = survivors.keySet().iterator();
		Location startLoc = players.next().getLocation();
		while (players.hasNext())
			players.next().teleport(startLoc);
		double theta = new Random().nextDouble() * 2 * Math.PI;
		hunterSpawn = startLoc.add(new Vector(300 * Math.cos(theta), 0, 300 * Math.sin(theta)));
		hunterSpawn = hunterSpawn.getWorld().getHighestBlockAt(hunterSpawn).getLocation().add(0, 1, 0);
		hunter.teleport(hunterSpawn);
	}
	
	private void setHunterInventory() {
		PlayerInventory pi = hunter.getInventory();
		pi.clear();
		pi.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		pi.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		pi.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		pi.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		pi.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
		pi.setItem(8, new ItemStack(Material.COMPASS));
		hunter.setBedSpawnLocation(hunterSpawn);
	}
	
	private void selectWinner(boolean survivorsWon) {
		if (survivorsWon)
			GameManager.broadcast(ChatColor.GREEN + "The survivor(s) have won trapcraft!");
		else GameManager.broadcast(ChatColor.DARK_RED + "The hunter has won trapcraft!");
		
	}

	@Override
	public boolean removePlayer(Player p) {
		if (hunter == p)
			selectWinner(true);
		else if (survivors.keySet().contains(p)) {
			survivors.remove(p);
			GameManager.broadcast(ChatColor.RED + p.getName() + "has been eliminated!");
			if (survivors.size() == 0)
				selectWinner(false);
		}
		else return false;
		return true;
	}
	
	public void onDeathEvent(Event event) {
		PlayerDeathEvent e = (PlayerDeathEvent) event;
		Player target = e.getEntity();
		if (hunter == target)
			this.setHunterInventory();
		//else removePlayer(target);
	}
	
	public void onRightClickEvent(Event event) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		Player player = e.getPlayer();
		if (survivors.containsKey(player))
			useTrap(player);
		else if (player.equals(hunter) && hunter.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
			++compassTarget;
			compassTarget %= survivors.keySet().size();
			Iterator<Player> iter = survivors.keySet().iterator();
			Player next = iter.next();
			for (int i = 0; i < compassTarget; ++i)
				next = iter.next();
			hunter.sendMessage("Compass now pointing to " + next.getName());
			hunterTarget = next;
		}
	}
	
	public void onEntityClickEvent(Event event) {
		PlayerInteractAtEntityEvent e = (PlayerInteractAtEntityEvent) event;
		Player player = e.getPlayer();
		if (survivors.containsKey(player))
			useTrap(player);
	}
	
	public void onPortalEntryEvent(Event event) {
		EntityPortalEnterEvent e = (EntityPortalEnterEvent) event;
		Player p = (Player) e.getEntity();
		if (p.getWorld().getBlockAt(e.getLocation()).getType() == Material.ENDER_PORTAL) {
			if (survivors.keySet().contains(p) && ! survivors.get(p)) {
				//GameManager.broadcast(ChatColor.AQUA + "" + ChatColor.BOLD + p.getName() + " has escaped!");
				//survivors.put(p, true);
				//for (Player pl : survivors.keySet())
				//	if (! survivors.get(pl))
				//		return;
				//this.selectWinner(true);
			}
		}
	}
	
	private void useTrap(Player p) {
		PlayerInventory pi = p.getInventory();
		switch(pi.getItemInMainHand().getType()) {
		case FLINT:
			hunter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, FREEZETIME * 20, 4));
			hunterLock = hunter.getLocation();
			cooldowns[1] = FREEZETIME * 20;
			reduce(pi);
			break;
		case IRON_INGOT:
			if (createTrap(p, false))
				reduce(pi);
			break;
		case GOLD_INGOT:
			reduce(pi);
			pi.addItem(new ItemStack(Material.ENDER_PEARL));
			break;
		case DIAMOND:
			if (createTrap(p, true))
				reduce(pi);
			break;
		default:
			break;
		}
	}
	
	private boolean createTrap(Player attempter, boolean big) {
		Location loc = attempter.getLocation();
		if (loc.distance(hunter.getLocation()) < 20) {
			attempter.sendMessage(ChatColor.RED + "Hunter is too close to set a trap!");
			return false;
		}
		for (Trap t : activeTraps)
			if (t.getLoc().distance(loc) < 20) {
				attempter.sendMessage(ChatColor.RED + "Trap is too close to another trap!");
				return false;
			}
		activeTraps.add(new Trap(loc, big));
		return true;
	}
	
	private void reduce(PlayerInventory pi) {
		if (pi.getItemInMainHand().getAmount() == 1)
			pi.setItemInMainHand(null);
		else pi.getItemInMainHand().setAmount(pi.getItemInMainHand().getAmount() - 1);
	}

}

class Trap {
	private Location l;
	private boolean big;
	
	public Trap(Location l, boolean big) {
		this.l = l;
		this.big = big;
	}
	
	public Location getLoc() {
		return l;
	}
	
	public boolean isBig() {
		return big;
	}
	
	public boolean trigger(Location hunterLoc) {
		if (hunterLoc.getWorld().equals(l.getWorld()) && hunterLoc.distance(l) <= 3)
			hunterLoc.getWorld().createExplosion(hunterLoc, big ? 8 : 3);
		else return false;
		return true;
	}
}
