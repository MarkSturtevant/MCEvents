package com.marks.mcevents.games;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.marks.mcevents.Main;

public class DeathSwap extends Game {
	
	private List<Player> players;
	private int gameTaskId;
	private int timer, catcher;
	
	public DeathSwap() {
		players = GameManager.getActivePlayers();
		if (players.size() < 2) {
			GameManager.broadcast("Deathswap needs at least two players!");
			GameManager.stopGame();
		}
	}

	@Override
	public void start() {
		timer = catcher = 0;
		gameTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			++timer;
			notifyTime();
		}, 0L, 1L);
	}
	
	private void notifyTime() {
		switch(catcher) {
		case 0:
			if (timer >= (60 * 4) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 1 minute!");
			}
			break;
		case 1:
			if (timer >= (60 * 4 + 50) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 10 seconds!");
			}
			break;
		case 2:
			if (timer >= (60 * 4 + 55) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 5 seconds!");
			}
			break;
		case 3:
			if (timer >= (60 * 4 + 56) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 4 seconds!");
			}
			break;
		case 4:
			if (timer >= (60 * 4 + 57) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 3 seconds!");
			}
			break;
		case 5:
			if (timer >= (60 * 4 + 58) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 2 seconds!");
			}
			break;
		case 6:
			if (timer >= (60 * 4 + 59) * 20) {
				catcher++;
				GameManager.broadcast("Swapping in 1 second!");
			}
			break;
		case 7:
			if (timer >= (60 * 5) * 20) {
				catcher = 0;
				timer = 0;
				this.swapPlayers();
			}
			break;
		}
	}
	
	private void swapPlayers() {
		GameManager.broadcast("Swapping!");
		Location temp = players.get(0).getLocation();
		for (int i = 0; i < players.size() - 1; ++i)
			players.get(i).teleport(players.get(i + 1));
		players.get(players.size() - 1).teleport(temp);
	}

	@Override
	public void stop() {
		Bukkit.getScheduler().cancelTask(gameTaskId);
	}

	@Override
	public boolean removePlayer(Player p) {
		if (! players.contains(p))
			return false;
		players.remove(p);
		if (players.size() == 1)
			declareWinner();
		return true;
	}
	
	private void declareWinner() {
		Player p = players.get(0);
		GameManager.broadcast("Player " + p.getName() + " has won deathswap!");
		GameManager.stopGame();
	}
	
	public void onDeathEvent(Event event) {
		PlayerDeathEvent e = (PlayerDeathEvent) event;
		Player target = e.getEntity();
		removePlayer(target);
	}

}
