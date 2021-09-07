package com.marks.mcevents.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.marks.mcevents.Main;

public class BuildSwap extends Game {
	
	private static final List<String> TITLES = Arrays.asList("Mark Sturtevant", "Jack's starter house", "a castle", "Saturn", "a realistic tree", "a dragon", "a fancy cake", "the void",
			"a Kirkland Complete Nutrition Shake", "Cosmo", "a violin", "Roblox", "breakfast", "a giant airpod", "a broken meter stick", "an octopus", "BuildSwap", "a volcano", "Codswallop", 
			"a rocket ship", "a satellite", "treasure", "Mark's real life house", "a golem", "a magic beanstalk", "AP Macroeconomics", "the big rock", "a pineapple", "a BigMac", "a hurricane", 
			"a flair pen");
	private static final List<String> INTROS = Arrays.asList("in a NERF war with", "dueling with", "destoying", "failing to build", "having a thoughtful conversation with", "eating", "on top of",
			"in a ZOOM call with", "looking at", "riding", "getting murdered by", "investigating", "holding", "oppressing", "throwing a party with", "dating", "playing Gets'Sturt with");
	
	private static final int MAX_TIME = 7 * 60 * 20;
	private ItemStack emerald, axe;
	
	private List<Build> builds;
	private int gameTaskId;
	private int timeLeft, catcher, phase, numFinished;
	
	public BuildSwap() {
		List<Player> players = GameManager.getActivePlayers();
		
		if (players.size() < 1) {
			GameManager.broadcast("BuildSwap needs at least one player!");
			GameManager.stopGame();
			return;
		}
		
		builds = new ArrayList<>();
		players.forEach(p -> builds.add(new Build(p)));
		phase = catcher = 0;
		
		emerald = new ItemStack(Material.EMERALD);
		axe = new ItemStack(Material.WOOD_AXE);
		ItemMeta im = emerald.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "I'm Finished!");
		emerald.setItemMeta(im);
		im = axe.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "WorldEdit Axe");
		axe.setItemMeta(im);
	}

	@Override
	public void start() {
		timeLeft = MAX_TIME;
		setInventories();
		gameTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			--timeLeft;
			if (catcher == 0 && timeLeft < 60 * 20) {
				++catcher;
				GameManager.broadcast(ChatColor.DARK_RED + "1 minute left to finish your builds!");
			}
			if (catcher == 1 && timeLeft < 10 * 20) {
				++catcher;
				GameManager.broadcast(ChatColor.DARK_RED + "" + ChatColor.BOLD + "10 seconds left to finish your builds!");
			}
			if (catcher == 2 && timeLeft == 0)
				this.nextRound();
		}, 0L, 1L);
	}
	
	private void setInventories() {
		for (Build b : builds) {
			PlayerInventory pi = b.p.getInventory();
			pi.clear();
			pi.setItem(0, axe);
			pi.setItem(8, emerald);
		}
	}

	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(gameTaskId);
	}
	
	private void nextRound() {
		++phase;
		setInventories();
		numFinished = 0;
		for (Build b : builds)
			b.finished = false;
		if (phase == 2) {
			for (Build b : builds)
				b.topic += " " + ChatColor.YELLOW + "whilst";
		} else if (phase == 3) {
			for (Build b : builds)
				b.spawnLoc = b.p.getLocation();
			GameManager.broadcast(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "It's time to judge!");
		}
		if (phase < 3) {
			catcher = 0;
			timeLeft = MAX_TIME;
			swapBuilds();
			for (Build b : builds)
				b.nextTopic();
		}
		else if (phase < 3 + builds.size()) {
			catcher = -1;
			for (Player p : Bukkit.getOnlinePlayers())
				p.teleport(builds.get(phase - 3).spawnLoc);
			GameManager.broadcast("Now, how is this for " + builds.get(phase - 3).topic + ChatColor.WHITE + "?");
		} else {
			GameManager.broadcast(ChatColor.DARK_AQUA + "This game has finished!  Vote on the best build.");
			GameManager.stopGame();
		}
	}
	
	private void swapBuilds() {
		for (Build b : builds)
			b.spawnLoc = b.p.getLocation();
		Player last = builds.get(builds.size() - 1).p;
		for (int i = builds.size() - 2; i >= 0; --i)
			builds.get(i + 1).p = builds.get(i).p;
		builds.get(0).p = last;
		for (Build b : builds)
			b.p.teleport(b.spawnLoc);
	}
	
	public void onRightClickEvent(Event event) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		Player player = e.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
			for (Build b : builds)
				if (b.p == player && ! b.finished) {
					b.finished = true;
					GameManager.broadcast(ChatColor.GREEN + player.getName() + " has finished their build!");
					if (++numFinished == builds.size())
						nextRound();
				}
		}
	}

	@Override
	public boolean removePlayer(Player p) {
		for (Build b : builds)
			if (b.p == p) {
				GameManager.broadcast(ChatColor.DARK_RED + "A participant has left the game; force ending buildswap.");
				GameManager.stopGame();
				return true;
			}
		return false;
	}
	
	private class Build {
		Player p;
		boolean finished;
		String topic;
		Location spawnLoc;
		
		public Build(Player p) {
			this.p = p;
			this.finished = false;
			topic = ChatColor.RED + TITLES.get(new Random().nextInt(TITLES.size()));
			p.sendMessage(ChatColor.GRAY + "Start this game by building " + topic);
			spawnLoc = null;
		}
		
		public void nextTopic() {
			this.finished = false;
			String addOn = " " + ChatColor.YELLOW + INTROS.get(new Random().nextInt(INTROS.size())) + ChatColor.RED + " " + TITLES.get(new Random().nextInt(TITLES.size()));
			p.sendMessage(ChatColor.GRAY + "Continue this build by depicting it" + addOn);
			topic += addOn;
		}
	}
	
}
