package com.marks.mcevents.games;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class GameManager {

	private static Game activeGame;
	
	public static boolean launchGame(String gameName) {
		switch(gameName) {
		case "deathswap":
			activeGame = new DeathSwap();
			break;
		case "blockshuffle":
			activeGame = new BlockShuffle();
			break;
		case "trapcraft":
			activeGame = new Trapcraft();
			break;
		case "buildswap":
			activeGame = new BuildSwap();
			break;
		case "solospeedrun":
			activeGame = new SoloSpeedrun();
			break;
		default:
			return false;
		}
		if (activeGame != null)
			activeGame.start();
		return true;
	}
	
	public static boolean isGameActive() {
		return activeGame != null;
	}
	
	public static void stopGame() {
		if (activeGame == null)
			return;
		activeGame.stop();
		activeGame = null;
	}
	
	public static boolean removePlayer(Player p) {
		if (activeGame != null)
			return activeGame.removePlayer(p);
		return false;
	}
	
	public static List<Player> getActivePlayers() {
		List<Player> players = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.getGameMode() != GameMode.SPECTATOR)
				players.add(p);
		return players;
	}
	
	public static void broadcast(String message) {
		for (Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(message);
	}
	
	public static void onEvent(String key, Event e) {
		if (activeGame == null)
			return;
		Method toFire;
		try {
			toFire = activeGame.getClass().getMethod(key, Event.class);
			if (toFire != null)
				toFire.invoke(activeGame, e);
		} catch (NoSuchMethodException e1) {
		} catch (Exception e2) {
			System.out.println("Error in event method");
		}
	}
	
}
